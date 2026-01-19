package com.example.gitgauge.data.repository

import com.example.gitgauge.data.db.UserSessionDao
import com.example.gitgauge.data.db.UserSessionEntity
import com.example.gitgauge.data.model.GithubUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSessionRepository @Inject constructor(
    private val userSessionDao: UserSessionDao
) {

    suspend fun saveUserSession(user: GithubUser) {
        val session = UserSessionEntity(
            id = 1,
            login = user.login,
            avatarUrl = user.avatarUrl,
            name = user.name,
            bio = user.bio,
            publicRepos = user.publicRepos,
            lastLoginTimestamp = System.currentTimeMillis(),
            isActive = true
        )
        userSessionDao.insertSession(session)
    }

    suspend fun getCurrentSession(): GithubUser? {
        return userSessionDao.getCurrentSession()?.let { entity ->
            GithubUser(
                id = 0,
                login = entity.login,
                avatarUrl = entity.avatarUrl,
                name = entity.name,
                bio = entity.bio,
                publicRepos = entity.publicRepos
            )
        }
    }

    fun getCurrentSessionFlow(): Flow<GithubUser?> {
        return userSessionDao.getCurrentSessionFlow().map { entity ->
            entity?.let {
                GithubUser(
                    id = 0,
                    login = it.login,
                    avatarUrl = it.avatarUrl,
                    name = it.name,
                    bio = it.bio,
                    publicRepos = it.publicRepos
                )
            }
        }
    }

    suspend fun clearUserSession() {
        userSessionDao.clearCurrentSession()
    }

    suspend fun hasActiveSession(): Boolean {
        return userSessionDao.hasActiveSession() > 0
    }

    suspend fun updateLastLoginTime() {
        userSessionDao.getCurrentSession()?.let { session ->
            userSessionDao.updateSession(
                session.copy(lastLoginTimestamp = System.currentTimeMillis())
            )
        }
    }
}
