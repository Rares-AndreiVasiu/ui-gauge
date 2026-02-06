package com.example.gitgauge.util

import com.example.gitgauge.data.model.Notification

fun Notification.formattedDisplayText(): String {
    return "AI analysis for $repoName completed"
}

fun Notification.displaySummary(): String {
    return "Repository: $repoName${if (repoOwner.isNotEmpty()) " by $repoOwner" else ""}"
}
