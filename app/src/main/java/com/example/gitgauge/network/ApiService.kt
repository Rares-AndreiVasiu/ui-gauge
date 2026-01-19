package com.example.gitgauge.network

import com.example.gitgauge.data.model.AnalysisResponse
import com.example.gitgauge.data.model.RepositoryItem
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("/auth/device/initiate")
    suspend fun initiateDeviceFlow(): DeviceFlowResponse

    @POST("/auth/device/poll")
    suspend fun pollDeviceFlow(
        @Query("device_code") device_code: String
    ): DevicePollResponse

    @GET("/login/url")
    suspend fun getLoginUrl(): LoginUrlResponse

    @GET("/auth/callback")
    suspend fun exchangeCodeForToken(
        @Query("code") code: String,
        @Query("state") state: String? = null
    ): AuthResponse

    @GET("/repos/list")
    suspend fun listRepositories(
        @Header("Authorization") token: String
    ): List<RepositoryItem>

    @POST("/repos/{owner}/{repo}/analyze")
    suspend fun analyzeRepository(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("owner") owner: String,
        @retrofit2.http.Path("repo") repo: String,
        @Body request: AnalyzeRepositoryRequest
    ): AnalysisResponse

    companion object {
        const val BASE_URL = "https://gitgauge.reuron.com/"
    }
}

data class DeviceFlowResponse(
    val device_code: String,
    val user_code: String,
    val verification_uri: String,
    val expires_in: Int,
    val interval: Int
)

data class DevicePollResponse(
    val status: String? = null,
    val access_token: String? = null,
    val user: DeviceFlowUser? = null
)

data class DeviceFlowUser(
    val login: String,
    val name: String? = null,
    val avatar_url: String? = null,
    val email: String? = null
)

data class LoginUrlResponse(
    val auth_url: String
)

data class AuthResponse(
    val access_token: String
)

data class AnalyzeRepositoryRequest(
    val owner: String,
    val repo: String,
    val ref: String = "main",
    val contents: List<String> = emptyList()
)


