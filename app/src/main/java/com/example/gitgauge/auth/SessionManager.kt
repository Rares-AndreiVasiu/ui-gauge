package com.example.gitgauge.auth

import android.content.Context
import com.example.gitgauge.data.model.GithubUser
import com.example.gitgauge.data.repository.UserSessionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val tokenManager: TokenManager,
    private val userSessionRepository: UserSessionRepository
) {

    suspend fun createSession(user: GithubUser, token: String) {
        // Save token securely in EncryptedSharedPreferences
        tokenManager.saveAccessToken(token)

        // Save user session data in Room database
        userSessionRepository.saveUserSession(user)
    }

    suspend fun restoreSession(): Pair<String, GithubUser>? {
        // Check if token exists
        val token = tokenManager.getAccessToken() ?: return null

        // Check if user session exists
        val user = userSessionRepository.getCurrentSession() ?: return null

        // Update last login time
        userSessionRepository.updateLastLoginTime()

        return Pair(token, user)
    }

    suspend fun hasValidSession(): Boolean {
        val hasToken = tokenManager.isTokenAvailable()
        val hasSession = userSessionRepository.hasActiveSession()
        return hasToken && hasSession
    }

    suspend fun logout() {
        // Clear token from EncryptedSharedPreferences
        tokenManager.clearAccessToken()

        // Clear user session from database
        userSessionRepository.clearUserSession()
    }

    suspend fun getStoredUser(): GithubUser? {
        return userSessionRepository.getCurrentSession()
    }

    fun getStoredUserFlow(): Flow<GithubUser?> {
        return userSessionRepository.getCurrentSessionFlow()
    }

    fun getStoredToken(): String? {
        return tokenManager.getAccessToken()
    }

    suspend fun updateSessionTimestamp() {
        userSessionRepository.updateLastLoginTime()
    }
}
