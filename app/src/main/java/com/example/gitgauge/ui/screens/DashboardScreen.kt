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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF13203b))
    ) {
        // Scrollable Content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .fillMaxWidth()
        ) {
            // User Info Card - Merged with Welcome Header
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
                    // Username Title - Gradient, Bold, 30sp
                    GradientText(
                        text = user.login ?: "User",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        colors = listOf(Color(0xFFf06bc7), Color(0xFFc67aff)),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    // User Full Name
                    Text(
                        text = user.name ?: "GitHub User",
                        fontSize = 14.sp,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Divider
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFF2a4068))

                    // Public Repositories Row
                    UserInfoRow("My public repositories", user.publicRepos.toString())

                    // Divider
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFF2a4068))

                    // Bio Row
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
                        .padding(32.dp),
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repositories.value.forEach { repo ->
                        RepositoryCard(
                            repo = repo,
                            onClick = {
                                onRepositoryClick(user.login, repo.name)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }

        // Bottom Navigation Bar - Darker color scheme
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0a121f))
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Settings Button
                Button(
                    onClick = onSettingsClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .padding(end = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2c3e50)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color(0xFFf06bc7),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "Settings",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Logout Button
                Button(
                    onClick = onLogout,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .padding(start = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8B0000)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Logout",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
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
