package com.example.gitgauge.data.repository

import com.example.gitgauge.data.db.NotificationDao
import com.example.gitgauge.data.model.Notification
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val notificationDao: NotificationDao
) {
    fun getAllNotifications(): Flow<List<Notification>> {
        return notificationDao.getAllNotifications()
    }

    fun getUnreadNotifications(): Flow<List<Notification>> {
        return notificationDao.getUnreadNotifications()
    }

    fun getUnreadNotificationCount(): Flow<Int> {
        return notificationDao.getUnreadNotificationCount()
    }

    suspend fun addNotification(notification: Notification) {
        notificationDao.insertNotification(notification)
    }

    suspend fun markAsRead(notificationId: String) {
        notificationDao.markAsRead(notificationId)
    }

    suspend fun deleteNotification(notification: Notification) {
        notificationDao.deleteNotification(notification)
    }

    suspend fun deleteAllNotifications() {
        notificationDao.deleteAllNotifications()
    }

    suspend fun updateNotification(notification: Notification) {
        notificationDao.updateNotification(notification)
    }
}
