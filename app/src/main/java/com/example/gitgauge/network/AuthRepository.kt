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
    private val gitHubApiService: GitHubApiService = NetworkModule.getGitHubApiService()

    suspend fun getLoginUrl(): String {
        return try {
            val response = apiService.getLoginUrl()
            response.auth_url
        } catch (e: Exception) {
            throw Exception("Failed to get login URL: ${e.message}")
        }
    }

    suspend fun exchangeCodeForToken(code: String, state: String? = null): Pair<String, GithubUser> {
        return try {
            // Exchange code for access token
            val response = apiService.exchangeCodeForToken(code, state)
            val token = response.accessToken

            // Save the token
            tokenManager.saveAccessToken(token)

            // Fetch user data from GitHub API
            val user = gitHubApiService.getCurrentUser("Bearer $token")

            Pair(token, user)
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
