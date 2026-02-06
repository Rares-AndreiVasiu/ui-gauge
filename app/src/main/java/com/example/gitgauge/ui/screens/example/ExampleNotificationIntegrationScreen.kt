package com.example.gitgauge.ui.screens.example

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.gitgauge.ui.components.NotificationFloatingButton
import com.example.gitgauge.ui.components.NotificationListScreen
import com.example.gitgauge.viewmodel.NotificationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExampleNotificationIntegrationScreen(
    notificationViewModel: NotificationViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showNotificationList by remember { mutableStateOf(false) }
    val unreadCount by notificationViewModel.unreadNotificationCount.collectAsState(initial = 0)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Notifications Example") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            NotificationFloatingButton(
                unreadCount = unreadCount,
                onClick = { showNotificationList = !showNotificationList }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (showNotificationList) {
                NotificationListScreen(
                    notifications = notificationViewModel.allNotifications,
                    onNotificationClick = { notificationId ->
                        notificationViewModel.markAsRead(notificationId)
                    },
                    onDeleteNotification = { notification ->
                        notificationViewModel.deleteNotification(notification)
                    },
                    onDeleteAllNotifications = {
                        notificationViewModel.deleteAllNotifications()
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
