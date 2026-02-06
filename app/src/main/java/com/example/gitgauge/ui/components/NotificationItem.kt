package com.example.gitgauge.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gitgauge.data.model.Notification
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationItem(
    notification: Notification,
    onNotificationClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    val formattedTime = dateFormat.format(Date(notification.timestamp))

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                if (notification.isRead) Color.Transparent else Color(0xFFF0F4FF)
            )
            .clickable(onClick = onNotificationClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Blue indicator for unread notifications
        if (!notification.isRead) {
            Box(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .background(Color(0xFF6366F1))
                    .padding(4.dp)
                    .background(Color(0xFF6366F1))
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = if (notification.isRead) 8.dp else 0.dp)
        ) {
            Text(
                text = notification.message,
                fontSize = 14.sp,
                fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = if (notification.isRead) Color.Gray else Color.Black
            )
            Text(
                text = formattedTime,
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        IconButton(onClick = onDeleteClick) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete notification",
                tint = Color.Gray
            )
        }
    }
}
