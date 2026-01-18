package com.example.gitgauge.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "analysis_cache")
data class AnalysisCacheEntity(
    @PrimaryKey
    val id: String,
    val owner: String,
    val repo: String,
    val summary: String,
    val analysis: String,
    val filesAnalyzed: Int,
    val ref: String,
    val timestamp: Long
)
