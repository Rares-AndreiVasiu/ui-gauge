package com.example.gitgauge.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gitgauge.data.model.Notification
import com.example.gitgauge.viewmodel.NotificationViewModel
import kotlinx.coroutines.flow.Flow

@Composable
fun NotificationListScreen(
    notifications: Flow<List<Notification>>,
    onNotificationClick: (String) -> Unit,
    onDeleteNotification: (Notification) -> Unit,
    onDeleteAllNotifications: () -> Unit,
    modifier: Modifier = Modifier
) {
    val notificationList by notifications.collectAsState(emptyList())

    Column(modifier = modifier.fillMaxWidth()) {
        if (notificationList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "No notifications",
                        tint = Color.Gray,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = "No notifications",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Button(onClick = onDeleteAllNotifications) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear all",
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text("Clear All")
                }
            }

            LazyColumn {
                items(notificationList) { notification ->
                    NotificationItem(
                        notification = notification,
                        onNotificationClick = { onNotificationClick(notification.id) },
                        onDeleteClick = { onDeleteNotification(notification) }
                    )
                    Divider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = Color.LightGray
                    )
                }
            }
        }
    }
}
