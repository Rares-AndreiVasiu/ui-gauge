package com.example.gitgauge.network

import com.example.gitgauge.auth.SessionManager
import com.example.gitgauge.auth.TokenManager
import com.example.gitgauge.data.model.GithubUser
import com.example.gitgauge.data.model.RepositoryItem
import com.example.gitgauge.data.repository.AnalysisCacheRepository
import kotlinx.coroutines.delay
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val tokenManager: TokenManager,
    private val cacheRepository: AnalysisCacheRepository,
    private val sessionManager: SessionManager,
    private val apiService: ApiService,
    private val gitHubApiService: GitHubApiService
) {

    suspend fun initiateDeviceFlow(): com.example.gitgauge.network.DeviceFlowResponse {
        return try {
            apiService.initiateDeviceFlow()
        } catch (e: HttpException) {
            if (e.code() == 404) {
                throw Exception("Device flow not available. Backend needs to be updated with device flow endpoints.")
            }
            throw Exception("Failed to initiate device flow: ${e.message}")
        } catch (e: Exception) {
            throw Exception("Failed to initiate device flow: ${e.message}")
        }
    }
    
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
            val tokenResponse = apiService.exchangeCodeForToken(code, state)
            val token = tokenResponse.access_token
            
            tokenManager.saveAccessToken(token)
            
            val user = gitHubApiService.getCurrentUser("Bearer $token")
            
            // Persist session for offline use
            sessionManager.createSession(user, token)

            Pair(token, user)
        } catch (e: Exception) {
            throw Exception("Failed to exchange code for token: ${e.message}")
        }
    }

    suspend fun pollDeviceFlow(deviceCode: String, intervalSeconds: Int): Pair<String, GithubUser> {
        while (true) {
            try {
                val response = apiService.pollDeviceFlow(deviceCode)
                
                when {
                    response.status == "pending" -> {
                        delay((intervalSeconds * 1000).toLong())
                        continue
                    }
                    response.status == "slow_down" -> {
                        delay((intervalSeconds * 1000 * 2).toLong())
                        continue
                    }
                    response.access_token != null && response.user != null -> {
                        val token = response.access_token
                        val deviceUser = response.user
                        
                        tokenManager.saveAccessToken(token)
                        
                        val user = GithubUser(
                            id = 0,
                            login = deviceUser.login,
                            avatarUrl = deviceUser.avatar_url ?: "",
                            name = deviceUser.name,
                            bio = null,
                            publicRepos = 0
                        )
                        
                        return Pair(token, user)
                    }
                    else -> {
                        throw Exception("Unexpected response from device flow")
                    }
                }
            } catch (e: Exception) {
                if (e.message?.contains("expired") == true) {
                    throw Exception("Device code expired. Please try again.")
                }
                throw Exception("Failed to poll device flow: ${e.message}")
            }
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

    suspend fun analyzeRepository(
        owner: String,
        repo: String,
        ref: String = "main",
        forceReanalysis: Boolean = false
    ): com.example.gitgauge.data.model.AnalysisResponse {
        return try {
            // Check cache first if force reanalysis is disabled
            if (!forceReanalysis) {
                val cachedAnalysis = cacheRepository.getAnalysis(owner, repo, ref)
                if (cachedAnalysis != null) {
                    return cachedAnalysis
                }
            }

            // If not in cache or force reanalysis is enabled, make API call
            val token = tokenManager.getAccessToken()
                ?: throw Exception("No access token available")
            val request = com.example.gitgauge.network.AnalyzeRepositoryRequest(
                owner = owner,
                repo = repo,
                ref = ref
            )
            val response = apiService.analyzeRepository("Bearer $token", owner, repo, request, forceReanalysis)

            // Save to cache
            cacheRepository.saveAnalysis(owner, repo, ref, response)

            response
        } catch (e: Exception) {
            // If API call fails, try to get cached result as fallback for offline support
            val cachedAnalysis = cacheRepository.getAnalysis(owner, repo, ref)
            if (cachedAnalysis != null) {
                // Return cached result even if not explicitly requested
                return cachedAnalysis
            }

            // If no cache available, throw the original error
            throw Exception("Failed to analyze repository: ${e.message}")
        }
    }

    fun getStoredAccessToken(): String? {
        return tokenManager.getAccessToken()
    }

    fun clearToken() {
        tokenManager.clearAccessToken()
    }

    suspend fun logout() {
        // Clear both token and session
        sessionManager.logout()
    }

    suspend fun restoreSessionFromStorage(): Pair<String, GithubUser>? {
        return sessionManager.restoreSession()
    }

    suspend fun hasValidOfflineSession(): Boolean {
        return sessionManager.hasValidSession()
    }

    suspend fun getStoredUserProfile(): GithubUser? {
        return sessionManager.getStoredUser()
    }

    fun isLoggedIn(): Boolean {
        return tokenManager.isTokenAvailable()
    }
}
