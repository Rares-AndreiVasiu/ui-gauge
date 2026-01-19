package com.example.gitgauge.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gitgauge.data.model.GithubUser
import com.example.gitgauge.data.model.RepositoryItem
import com.example.gitgauge.network.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading) // Start with Loading to check session
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(authRepository.isLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _repositories = MutableStateFlow<List<RepositoryItem>>(emptyList())
    val repositories: StateFlow<List<RepositoryItem>> = _repositories.asStateFlow()

    private val _forceReanalysis = MutableStateFlow(false)
    val forceReanalysis: StateFlow<Boolean> = _forceReanalysis.asStateFlow()

    init {
        // Immediately attempt to restore session synchronously if possible
        viewModelScope.launch {
            attemptSessionRestoration()
        }
    }

    fun attemptSessionRestoration() {
        // Try to restore session from storage for offline use
        viewModelScope.launch {
            try {
                // First check if token exists (fast check)
                if (!authRepository.isLoggedIn()) {
                    _authState.value = AuthState.Idle
                    _isLoggedIn.value = false
                    return@launch
                }

                // Check if we have a valid offline session
                val hasValidSession = authRepository.hasValidOfflineSession()
                if (hasValidSession) {
                    val session = authRepository.restoreSessionFromStorage()
                    if (session != null) {
                        val (token, user) = session
                        _authState.value = AuthState.Success(user)
                        _isLoggedIn.value = true
                        // Load repositories (will use cache if offline)
                        loadRepositories()
                        return@launch
                    }
                }
                // If token exists but no valid session, set to Idle
                _authState.value = AuthState.Idle
                _isLoggedIn.value = false
            } catch (e: Exception) {
                // Silent fail - user will need to login if session restoration fails
                // Set to Idle so user can attempt login
                _authState.value = AuthState.Idle
                _isLoggedIn.value = false
            }
        }
    }

    fun startGithubLogin() {
        viewModelScope.launch {
            // Before attempting login, check if we have a valid offline session
            val hasValidSession = authRepository.hasValidOfflineSession()
            if (hasValidSession) {
                // If we have a valid session, restore it instead of trying to login
                attemptSessionRestoration()
                return@launch
            }

            _authState.value = AuthState.Loading
            try {
                val loginUrl = authRepository.getLoginUrl()
                _authState.value = AuthState.WebViewLogin(loginUrl)
            } catch (e: Exception) {
                // If login fails, try to restore session as fallback
                val hasOfflineSession = authRepository.hasValidOfflineSession()
                if (hasOfflineSession) {
                    attemptSessionRestoration()
                } else {
                    _authState.value = AuthState.Error(e.message ?: "Failed to start login")
                }
            }
        }
    }
    
    fun handleAuthCallback(code: String, state: String? = null) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val (token, user) = authRepository.exchangeCodeForToken(code, state)
                _authState.value = AuthState.Success(user)
                _isLoggedIn.value = true
                loadRepositories()
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Authentication failed")
                _isLoggedIn.value = false
            }
        }
    }

    fun loadRepositories() {
        viewModelScope.launch {
            try {
                val repos = authRepository.listRepositories()
                _repositories.value = repos
                // Don't set error state if we successfully got repos (even from cache)
                // Only update auth state if we're not already in Success state
                if (_authState.value !is AuthState.Success) {
                    // If we have repos but no user in state, try to get stored user
                    val storedUser = authRepository.getStoredUserProfile()
                    if (storedUser != null) {
                        _authState.value = AuthState.Success(storedUser)
                        _isLoggedIn.value = true
                    }
                }
            } catch (e: Exception) {
                // If we're already logged in (have a valid session), don't show error
                // Just use empty list or existing cached repos
                if (_authState.value is AuthState.Success) {
                    // We have a valid session but couldn't load repos
                    // Keep the success state and just show empty list or keep existing repos
                    // Don't change auth state to error - user is still logged in
                    if (_repositories.value.isEmpty()) {
                        // Try to get any cached repos one more time
                    try {
                        val cachedRepos = authRepository.getCachedRepositories()
                        _repositories.value = cachedRepos
                    } catch (cacheException: Exception) {
                        // No cache available, just keep empty list
                        _repositories.value = emptyList()
                    }
                    }
                } else {
                    // Only show error if we're not logged in
                    if (_repositories.value.isEmpty()) {
                        _authState.value = AuthState.Error("Failed to load repositories: ${e.message}")
                    }
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _isLoggedIn.value = false
            _authState.value = AuthState.Idle
            _repositories.value = emptyList()
        }
    }

    fun toggleForceReanalysis() {
        _forceReanalysis.value = !_forceReanalysis.value
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class WebViewLogin(val loginUrl: String) : AuthState()
    data class Success(val user: GithubUser) : AuthState()
    data class Error(val message: String) : AuthState()
}
