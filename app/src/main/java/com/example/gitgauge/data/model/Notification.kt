package com.example.gitgauge.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey
    val id: String,
    val repoName: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val repoOwner: String = "",
    val analysisType: String = "repository_analysis"
)

data class NotificationPayload(
    @SerializedName("id")
    val id: String,
    @SerializedName("repo_name")
    val repoName: String,
    @SerializedName("repo_owner")
    val repoOwner: String? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("analysis_type")
    val analysisType: String = "repository_analysis"
)
