package com.example.gitgauge.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.gitgauge.network.NotificationService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Background service that maintains WebSocket connection to notification server
 * even when the app is closed or in the background
 */
@AndroidEntryPoint
class NotificationBackgroundService : Service() {

    @Inject
    lateinit var notificationService: NotificationService

    private val binder = LocalBinder()
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val tag = "NotificationBGService"

    inner class LocalBinder : Binder() {
        fun getService(): NotificationBackgroundService = this@NotificationBackgroundService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(tag, "Service started, connecting to notification server...")

        // Connect to WebSocket server
        scope.launch {
            try {
                notificationService.connectToNotificationServer("wss://gitgauge.reuron.com/notifications")
                Log.d(tag, "Connected to notification server")
            } catch (e: Exception) {
                Log.e(tag, "Failed to connect to notification server", e)
            }
        }

        // Keep service running
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(tag, "Service destroyed, disconnecting from notification server...")
        notificationService.disconnectFromNotificationServer()
    }
}
