package com.example.gitgauge.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: UserSessionEntity)

    @Query("SELECT * FROM user_sessions WHERE id = 1")
    suspend fun getCurrentSession(): UserSessionEntity?

    @Query("SELECT * FROM user_sessions WHERE id = 1")
    fun getCurrentSessionFlow(): kotlinx.coroutines.flow.Flow<UserSessionEntity?>

    @Update
    suspend fun updateSession(session: UserSessionEntity)

    @Delete
    suspend fun deleteSession(session: UserSessionEntity)

    @Query("DELETE FROM user_sessions WHERE id = 1")
    suspend fun clearCurrentSession()

    @Query("SELECT COUNT(*) FROM user_sessions WHERE id = 1 AND isActive = 1")
    suspend fun hasActiveSession(): Int
}
