package com.example.gitgauge.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AnalysisCacheEntity::class, UserSessionEntity::class], version = 2)
abstract class GitgaugeDatabase : RoomDatabase() {

    abstract fun analysisCacheDao(): AnalysisCacheDao
    abstract fun userSessionDao(): UserSessionDao

    companion object {
        @Volatile
        private var instance: GitgaugeDatabase? = null

        fun getInstance(context: Context): GitgaugeDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    GitgaugeDatabase::class.java,
                    "gitgauge_database"
                )
                .fallbackToDestructiveMigration()
                .build().also { instance = it }
            }
        }
    }
}
