package com.example.gitgauge.network

import com.example.gitgauge.data.model.GithubUser
import retrofit2.http.GET
import retrofit2.http.Header

interface GitHubApiService {

    @GET("user")
    suspend fun getCurrentUser(
        @Header("Authorization") token: String
    ): GithubUser

    companion object {
        const val BASE_URL = "https://api.github.com/"
    }
}
