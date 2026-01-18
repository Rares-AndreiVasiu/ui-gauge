package com.example.gitgauge.data.model

import com.google.gson.annotations.SerializedName

data class GithubUser(
    @SerializedName("id")
    val id: Int,
    @SerializedName("login")
    val login: String,
    @SerializedName("avatar_url")
    val avatarUrl: String,
    @SerializedName("name")
    val name: String?,
    @SerializedName("bio")
    val bio: String?,
    @SerializedName("public_repos")
    val publicRepos: Int
)

data class AuthResponse(
    @SerializedName("access_token")
    val accessToken: String
)

data class RepositoryItem(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String?,
    @SerializedName("html_url")
    val htmlUrl: String,
    @SerializedName("stargazers_count")
    val starsCount: Int
)

data class SimpleRepository(
    @SerializedName("owner")
    val owner: String,
    @SerializedName("repo")
    val repo: String
)

