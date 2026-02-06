package com.example.gitgauge.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gitgauge.data.model.Notification

@Database(entities = [AnalysisCacheEntity::class, UserSessionEntity::class, Notification::class], version = 3)
abstract class GitgaugeDatabase : RoomDatabase() {

    abstract fun analysisCacheDao(): AnalysisCacheDao
    abstract fun userSessionDao(): UserSessionDao
    abstract fun notificationDao(): NotificationDao

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
