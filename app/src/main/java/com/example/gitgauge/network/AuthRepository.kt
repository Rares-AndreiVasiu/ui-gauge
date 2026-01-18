package com.example.gitgauge.network

import com.example.gitgauge.auth.TokenManager
import com.example.gitgauge.data.model.AuthResponse
import com.example.gitgauge.data.model.GithubUser
import com.example.gitgauge.data.model.RepositoryItem
import com.example.gitgauge.di.NetworkModule
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val tokenManager: TokenManager
) {

    private val apiService: ApiService = NetworkModule.getApiService()

    suspend fun getLoginUrl(): String {
        return try {
            val response = apiService.getLoginUrl()
            response.loginUrl
        } catch (e: Exception) {
            throw Exception("Failed to get login URL: ${e.message}")
        }
    }

    suspend fun exchangeCodeForToken(code: String, state: String? = null): Pair<String, GithubUser> {
        return try {
            val response = apiService.exchangeCodeForToken(code, state)
            tokenManager.saveAccessToken(response.accessToken)
            Pair(response.accessToken, response.userJson)
        } catch (e: Exception) {
            throw Exception("Failed to exchange code for token: ${e.message}")
        }
    }

    suspend fun listRepositories(): List<RepositoryItem> {
        return try {
            val token = tokenManager.getAccessToken()
                ?: throw Exception("No access token available")
            apiService.listRepositories("Bearer $token")
        } catch (e: Exception) {
            throw Exception("Failed to list repositories: ${e.message}")
        }
    }

    fun getStoredAccessToken(): String? {
        return tokenManager.getAccessToken()
    }

    fun clearToken() {
        tokenManager.clearAccessToken()
    }

    fun isLoggedIn(): Boolean {
        return tokenManager.isTokenAvailable()
    }
}
