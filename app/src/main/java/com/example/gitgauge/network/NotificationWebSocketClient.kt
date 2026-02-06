package com.example.gitgauge.network

import android.util.Log
import com.example.gitgauge.data.model.Notification
import com.example.gitgauge.data.model.NotificationPayload
import com.example.gitgauge.data.repository.NotificationRepository
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationWebSocketClient @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val pushNotificationManager: PushNotificationManager,
    private val okHttpClient: OkHttpClient
) : WebSocketListener() {

    private var webSocket: WebSocket? = null
    private val gson = Gson()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val tag = "NotificationWS"

    private var reconnectAttempts = 0
    private val maxReconnectAttempts = 5
    private val reconnectDelayMs = 3000L
    private var notificationCounter = 0

    fun connect(serverUrl: String) {
        try {
            val request = Request.Builder()
                .url(serverUrl)
                .build()
            webSocket = okHttpClient.newWebSocket(request, this)
            Log.d(tag, "Attempting to connect to WebSocket: $serverUrl")
        } catch (e: Exception) {
            Log.e(tag, "Error initiating WebSocket connection", e)
            scheduleReconnect(serverUrl)
        }
    }

    override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
        Log.d(tag, "WebSocket connected")
        reconnectAttempts = 0
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        try {
            Log.d(tag, "Message received: $text")
            val payload = gson.fromJson(text, NotificationPayload::class.java)
            val message = payload.message ?: "AI analysis for ${payload.repoName} completed"
            val notification = Notification(
                id = payload.id.ifEmpty { UUID.randomUUID().toString() },
                repoName = payload.repoName,
                message = message,
                repoOwner = payload.repoOwner ?: "",
                analysisType = payload.analysisType
            )
            scope.launch {
                notificationRepository.addNotification(notification)
                Log.d(tag, "Notification saved: ${notification.repoName}")

                // Send system push notification
                notificationCounter++
                pushNotificationManager.sendPushNotification(
                    title = "Repository Analysis Complete",
                    message = message,
                    notificationId = notificationCounter
                )
            }
        } catch (e: Exception) {
            Log.e(tag, "Error processing notification message", e)
        }
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
        Log.e(tag, "WebSocket failure: ${t.message}", t)
        // Try to get URL from the request
        val url = try {
            webSocket.request().url.toString()
        } catch (e: Exception) {
            "unknown"
        }
        scheduleReconnect(url)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        Log.d(tag, "WebSocket closed with code $code: $reason")
        // Try to get URL from the request
        val url = try {
            webSocket.request().url.toString()
        } catch (e: Exception) {
            "unknown"
        }
        scheduleReconnect(url)
    }

    private fun scheduleReconnect(serverUrl: String) {
        if (reconnectAttempts < maxReconnectAttempts) {
            reconnectAttempts++
            scope.launch {
                Log.d(tag, "Scheduling reconnect attempt $reconnectAttempts after ${reconnectDelayMs}ms")
                delay(reconnectDelayMs)
                if (scope.isActive) {
                    connect(serverUrl)
                }
            }
        } else {
            Log.e(tag, "Max reconnect attempts reached")
        }
    }

    fun disconnect() {
        webSocket?.close(1000, "Client disconnecting")
        webSocket = null
        Log.d(tag, "WebSocket disconnected")
    }

    fun isConnected(): Boolean = webSocket != null
}
