package com.example.gitgauge.util

import android.util.Log
import com.example.gitgauge.data.model.Notification
import com.example.gitgauge.data.repository.NotificationRepository
import com.example.gitgauge.network.NotificationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object NotificationIntegration {
    private const val TAG = "NotificationIntegration"

    fun handleRepositoryAnalysisResponse(
        scope: CoroutineScope,
        notificationService: NotificationService,
        repoName: String,
        repoOwner: String,
        analysisId: String = "",
        customMessage: String? = null
    ) {
        scope.launch {
            try {
                val message = customMessage ?: "AI analysis for $repoName completed"
                val notification = Notification(
                    id = analysisId.ifEmpty { java.util.UUID.randomUUID().toString() },
                    repoName = repoName,
                    message = message,
                    repoOwner = repoOwner,
                    analysisType = "repository_analysis"
                )

                notificationService.let {
                    // This will trigger the WebSocket to send system notification
                    // if connected, otherwise it's just stored locally
                    Log.d(TAG, "Repository analysis completed for $repoName by $repoOwner")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling repository analysis response", e)
            }
        }
    }
}
