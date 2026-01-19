package com.example.gitgauge.di

import android.content.Context
import com.example.gitgauge.data.db.AnalysisCacheDao
import com.example.gitgauge.data.db.GitgaugeDatabase
import com.example.gitgauge.data.db.UserSessionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideGitgaugeDatabase(
        @ApplicationContext context: Context
    ): GitgaugeDatabase {
        return GitgaugeDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideAnalysisCacheDao(
        database: GitgaugeDatabase
    ): AnalysisCacheDao {
        return database.analysisCacheDao()
    }

    @Provides
    @Singleton
    fun provideUserSessionDao(
        database: GitgaugeDatabase
    ): UserSessionDao {
        return database.userSessionDao()
    }
}
