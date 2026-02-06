package com.example.gitgauge.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.gitgauge.service.NotificationBackgroundService

/**
 * BroadcastReceiver that starts the notification service when:
 * 1. Device boots up
 * 2. App is installed/updated
 * 3. App is killed and needs to be restarted
 */
class NotificationServiceRestartReceiver : BroadcastReceiver() {

    private val tag = "NotificationRestartRx"

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return

        Log.d(tag, "Broadcast received: ${intent?.action}")

        val serviceIntent = Intent(context, NotificationBackgroundService::class.java)

        when (intent?.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                Log.d(tag, "Device booted, starting notification service")
                context.startService(serviceIntent)
            }
            "com.example.gitgauge.RESTART_NOTIFICATION_SERVICE" -> {
                Log.d(tag, "Restart signal received, starting notification service")
                context.startService(serviceIntent)
            }
        }
    }
}
