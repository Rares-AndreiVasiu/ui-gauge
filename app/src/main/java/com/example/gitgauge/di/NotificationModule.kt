package com.example.gitgauge.di

import com.example.gitgauge.data.db.GitgaugeDatabase
import com.example.gitgauge.data.db.NotificationDao
import com.example.gitgauge.data.repository.NotificationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {

    @Singleton
    @Provides
    fun provideNotificationDao(database: GitgaugeDatabase): NotificationDao {
        return database.notificationDao()
    }

    @Singleton
    @Provides
    fun provideNotificationRepository(notificationDao: NotificationDao): NotificationRepository {
        return NotificationRepository(notificationDao)
    }
}
