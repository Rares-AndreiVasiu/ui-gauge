package com.example.gitgauge.data.model

import com.google.gson.annotations.SerializedName

data class AnalysisResponse(
    @SerializedName("summary")
    val summary: String,
    @SerializedName("analysis")
    val analysis: String,
    @SerializedName("repository")
    val repository: RepositoryAnalysisInfo
)

data class RepositoryAnalysisInfo(
    @SerializedName("owner")
    val owner: String,
    @SerializedName("repo")
    val repo: String,
    @SerializedName("ref")
    val ref: String,
    @SerializedName("files_analyzed")
    val filesAnalyzed: Int
)
