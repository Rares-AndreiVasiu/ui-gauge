package com.example.gitgauge.network

import com.example.gitgauge.data.model.Notification
import com.example.gitgauge.data.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationService @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val webSocketClient: NotificationWebSocketClient
) {

    fun getAllNotifications(): Flow<List<Notification>> {
        return notificationRepository.getAllNotifications()
    }

    fun getUnreadNotifications(): Flow<List<Notification>> {
        return notificationRepository.getUnreadNotifications()
    }

    fun getUnreadNotificationCount(): Flow<Int> {
        return notificationRepository.getUnreadNotificationCount()
    }

    suspend fun markNotificationAsRead(notificationId: String) {
        notificationRepository.markAsRead(notificationId)
    }

    suspend fun deleteNotification(notification: Notification) {
        notificationRepository.deleteNotification(notification)
    }

    suspend fun deleteAllNotifications() {
        notificationRepository.deleteAllNotifications()
    }

    fun connectToNotificationServer(serverUrl: String) {
        webSocketClient.connect(serverUrl)
    }

    fun disconnectFromNotificationServer() {
        webSocketClient.disconnect()
    }

    fun isConnectedToNotificationServer(): Boolean {
        return webSocketClient.isConnected()
    }
}
