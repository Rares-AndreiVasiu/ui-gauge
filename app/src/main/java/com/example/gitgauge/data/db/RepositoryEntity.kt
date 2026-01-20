package com.example.gitgauge.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repositories")
data class RepositoryEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val description: String?,
    val htmlUrl: String,
    val starsCount: Int
)

