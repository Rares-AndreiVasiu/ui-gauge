package com.example.gitgauge.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_sessions")
data class UserSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 1, // Single row to store current session
    val login: String,
    val avatarUrl: String,
    val name: String?,
    val bio: String?,
    val publicRepos: Int,
    val lastLoginTimestamp: Long,
    val isActive: Boolean = true
)
