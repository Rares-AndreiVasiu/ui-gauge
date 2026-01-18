package com.example.gitgauge.network

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface GitHubOAuthService {

    /**
     * Exchange authorization code for access token via backend
     * Backend handles the Client ID and Secret securely
     */
    @POST("/auth/token")
    suspend fun exchangeCodeForToken(
        @Query("code") code: String,
        @Query("state") state: String? = null
    ): TokenExchangeResponse

    /**
     * Revoke token when user logs out
     */
    @POST("/auth/revoke")
    suspend fun revokeToken(
        @Header("Authorization") token: String
    ): RevokeResponse

    companion object {
        const val BASE_URL = "http://gitgauge.reuron.com/"
    }
}

data class TokenExchangeResponse(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("token_type")
    val tokenType: String = "bearer",
    @SerializedName("expires_in")
    val expiresIn: Long? = null
)

data class RevokeResponse(
    val success: Boolean,
    val message: String? = null
)
