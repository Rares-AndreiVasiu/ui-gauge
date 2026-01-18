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

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        _isLoggedIn.value = authRepository.isLoggedIn()
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
        authRepository.clearToken()
        _isLoggedIn.value = false
        _authState.value = AuthState.Idle
        _repositories.value = emptyList()
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
