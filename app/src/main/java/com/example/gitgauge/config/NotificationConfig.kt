package com.example.gitgauge.config

object NotificationConfig {
    const val WEB_SOCKET_URL = "wss://gitgauge.reuron.com/notifications"
    const val RECONNECT_DELAY_MS = 3000L
    const val MAX_RECONNECT_ATTEMPTS = 5
    const val NOTIFICATION_EXPIRY_DAYS = 30
}
