package com.example.gitgauge.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gitgauge.data.model.GithubUser
import com.example.gitgauge.data.model.RepositoryItem
import com.example.gitgauge.ui.components.GradientText
import com.example.gitgauge.viewmodel.AuthViewModel

@Composable
fun DashboardScreen(
    viewModel: AuthViewModel,
    user: GithubUser,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    onRepositoryClick: (owner: String, repo: String) -> Unit = { _, _ -> },
    onSettingsClick: () -> Unit = {}
) {
    val repositories = viewModel.repositories.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF13203b))
    ) {
        // Top Navigation Bar with Logout and Settings
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0f1621))
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Settings Button
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.padding(end = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color(0xFFf06bc7),
                    modifier = Modifier
                )
            }

            // Logout Button
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .height(40.dp)
                    .padding(end = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFDC3545)
                )
            ) {
                Text(
                    text = "Logout",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0f1621))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Welcome, ${user.login ?: "User"}!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = user.name ?: "GitHub User",
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }

        // User Info Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1a2d47)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                UserInfoRow("My public repositories", user.publicRepos.toString())
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFF2a4068))
                UserInfoRow("Bio", user.bio ?: "No bio")
            }
        }

        // Repositories Section
        Text(
            text = "Public Repositories (${repositories.value.size})",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        if (repositories.value.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFFf06bc7),
                        modifier = Modifier.padding(32.dp)
                    )
                    Text(
                        text = "Loading repositories...",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(repositories.value) { repo ->
                    RepositoryCard(
                        repo = repo,
                        onClick = {
                            // Navigate to analysis screen instead of GitHub
                            onRepositoryClick(user.login, repo.name)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun UserInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        GradientText(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            colors = listOf(Color(0xFFf06bc7), Color(0xFFc67aff))
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun RepositoryCard(
    repo: RepositoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1a2d47)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(9.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            GradientText(
                text = repo.name ?: "Unnamed Repository",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                colors = listOf(Color(0xFFf06bc7), Color(0xFFc67aff)),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            if (!repo.description.isNullOrEmpty()) {
                Text(
                    text = repo.description ?: "",
                    fontSize = 13.sp,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp),
                    maxLines = 2
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⭐ ${repo.starsCount ?: 0}",
                    fontSize = 12.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "View Repository",
                    fontSize = 12.sp,
                    color = Color(0xFFf06bc7),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
