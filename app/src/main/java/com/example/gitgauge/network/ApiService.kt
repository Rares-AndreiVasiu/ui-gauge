package com.example.gitgauge.network

import com.example.gitgauge.data.model.AuthResponse
import com.example.gitgauge.data.model.RepositoryItem
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiService {

    @GET("/login")
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

    companion object {
        const val BASE_URL = "http://localhost:8000/" // Change to your backend URL
    }
}

data class LoginUrlResponse(
    val loginUrl: String
)
