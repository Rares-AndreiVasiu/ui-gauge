package com.example.gitgauge.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RepositoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepositories(repositories: List<RepositoryEntity>)

    @Query("SELECT * FROM repositories")
    suspend fun getAllRepositories(): List<RepositoryEntity>

    @Query("DELETE FROM repositories")
    suspend fun clearRepositories()

    @Query("SELECT COUNT(*) FROM repositories")
    suspend fun getRepositoryCount(): Int
}

