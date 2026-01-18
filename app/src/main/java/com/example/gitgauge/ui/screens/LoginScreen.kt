package com.example.gitgauge.ui.screens

import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.gitgauge.viewmodel.AuthState
import com.example.gitgauge.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val authState = viewModel.authState.collectAsState()

    when (val state = authState.value) {
        is AuthState.WebViewLogin -> {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                url: String?
                            ): Boolean {
                                if (url != null && url.contains("/auth/callback") && url.contains("code=")) {
                                    val uri = Uri.parse(url)
                                    val code = uri.getQueryParameter("code")
                                    val state = uri.getQueryParameter("state")
                                    if (code != null) {
                                        viewModel.handleAuthCallback(code, state)
                                    }
                                    return true
                                }
                                return false
                            }
                        }
                        loadUrl(state.loginUrl)
                    }
                },
                modifier = modifier.fillMaxSize()
            )
        }
        else -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color(0xFFF6F8FA)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Gitgauge",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF24292F),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Text(
                        text = "GitHub Repository Insights",
                        fontSize = 16.sp,
                        color = Color(0xFF57606A),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    when (val state = authState.value) {
                        is AuthState.Idle -> {
                            LoginButton(
                                onClick = { viewModel.startGithubLogin() },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        is AuthState.Loading -> {
                            CircularProgressIndicator(
                                color = Color(0xFF24292F),
                                modifier = Modifier.padding(32.dp)
                            )
                            Text(
                                text = "Preparing login...",
                                color = Color(0xFF57606A),
                                fontSize = 14.sp
                            )
                        }

                        is AuthState.Success -> {
                            Text(
                                text = "✓ Connected Successfully!",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF28A745),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "Welcome, ${state.user.login ?: "User"}!",
                                fontSize = 16.sp,
                                color = Color(0xFF24292F)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            CircularProgressIndicator(
                                color = Color(0xFF24292F)
                            )
                            Text(
                                text = "Loading repositories...",
                                fontSize = 14.sp,
                                color = Color(0xFF57606A),
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }

                        is AuthState.Error -> {
                            Text(
                                text = "Authentication Error",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFDC3545),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = state.message ?: "An unknown error occurred",
                                fontSize = 14.sp,
                                color = Color(0xFF57606A),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 24.dp)
                            )
                            LoginButton(
                                onClick = { viewModel.resetAuthState() },
                                modifier = Modifier.fillMaxWidth(),
                                text = "Try Again"
                            )
                        }

                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
private fun LoginButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "Login with GitHub"
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF24292F)
        )
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
