package com.example.gitgauge.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gitgauge.data.model.Notification
import com.example.gitgauge.network.NotificationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationService: NotificationService
) : ViewModel() {

    val allNotifications: Flow<List<Notification>> = notificationService.getAllNotifications()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val unreadNotifications: Flow<List<Notification>> = notificationService.getUnreadNotifications()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val unreadNotificationCount: Flow<Int> = notificationService.getUnreadNotificationCount()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)


    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            notificationService.markNotificationAsRead(notificationId)
        }
    }

    fun deleteNotification(notification: Notification) {
        viewModelScope.launch {
            notificationService.deleteNotification(notification)
        }
    }

    fun deleteAllNotifications() {
        viewModelScope.launch {
            notificationService.deleteAllNotifications()
        }
    }

    fun connectToNotificationServer(serverUrl: String) {
        notificationService.connectToNotificationServer(serverUrl)
    }

    fun disconnectFromNotificationServer() {
        notificationService.disconnectFromNotificationServer()
    }

    fun isConnectedToServer(): Boolean {
        return notificationService.isConnectedToNotificationServer()
    }
}
