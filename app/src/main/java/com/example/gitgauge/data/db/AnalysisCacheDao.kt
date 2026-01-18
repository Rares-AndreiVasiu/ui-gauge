package com.example.gitgauge.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AnalysisCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalysis(analysis: AnalysisCacheEntity)

    @Query("SELECT * FROM analysis_cache WHERE owner = :owner AND repo = :repo AND ref = :ref")
    suspend fun getAnalysis(owner: String, repo: String, ref: String): AnalysisCacheEntity?

    @Query("SELECT * FROM analysis_cache")
    suspend fun getAllAnalysis(): List<AnalysisCacheEntity>

    @Delete
    suspend fun deleteAnalysis(analysis: AnalysisCacheEntity)

    @Query("DELETE FROM analysis_cache")
    suspend fun clearCache()

    @Query("DELETE FROM analysis_cache WHERE timestamp < :expirationTime")
    suspend fun clearOldAnalysis(expirationTime: Long)
}
