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

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(authRepository.isLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _repositories = MutableStateFlow<List<RepositoryItem>>(emptyList())
    val repositories: StateFlow<List<RepositoryItem>> = _repositories.asStateFlow()

    private val _forceReanalysis = MutableStateFlow(false)
    val forceReanalysis: StateFlow<Boolean> = _forceReanalysis.asStateFlow()

    init {
        checkLoginStatus()
        attemptSessionRestoration()
    }

    private fun checkLoginStatus() {
        _isLoggedIn.value = authRepository.isLoggedIn()
    }

    private fun attemptSessionRestoration() {
        // Try to restore session from storage for offline use
        viewModelScope.launch {
            try {
                val session = authRepository.restoreSessionFromStorage()
                if (session != null) {
                    val (token, user) = session
                    _authState.value = AuthState.Success(user)
                    _isLoggedIn.value = true
                    loadRepositories()
                }
            } catch (e: Exception) {
                // Silent fail - user will need to login if session restoration fails
            }
        }
    }

    fun startGithubLogin() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val loginUrl = authRepository.getLoginUrl()
                _authState.value = AuthState.WebViewLogin(loginUrl)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Failed to start login")
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
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Failed to load repositories: ${e.message}")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                authRepository.logout()
                _isLoggedIn.value = false
                _authState.value = AuthState.Idle
                _repositories.value = emptyList()
                _forceReanalysis.value = false
            } catch (e: Exception) {
                // Log the error but still reset the UI state
                _isLoggedIn.value = false
                _authState.value = AuthState.Idle
                _repositories.value = emptyList()
                _forceReanalysis.value = false
            }
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
