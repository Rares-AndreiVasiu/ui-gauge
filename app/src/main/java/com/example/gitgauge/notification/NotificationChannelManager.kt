package com.example.gitgauge.notification

import android.app.NotificationChannel
import android.content.Context
import android.os.Build
import com.example.gitgauge.network.PushNotificationManager

object NotificationChannelManager {
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                PushNotificationManager.CHANNEL_ID,
                "Gitgauge Notifications",
                android.app.NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for repository analysis completion"
                enableLights(true)
                enableVibration(true)
            }

            val notificationManager = context.getSystemService(android.app.NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
