package com.example.gitgauge.ui.components

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.gitgauge.auth.GitHubOAuthWebViewClient

@Composable
fun GitHubOAuthWebView(
    loginUrl: String,
    onCodeReceived: (code: String, state: String?) -> Unit,
    onError: (error: String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLoading = remember { mutableStateOf(true) }
    val error = remember { mutableStateOf<String?>(null) }
    val webViewRef = remember { mutableStateOf<WebView?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        },
        modifier = modifier.fillMaxWidth(0.95f),
        title = { Text("GitHub Login") },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp),
                contentAlignment = Alignment.Center
            ) {
                if (error.value != null) {
                    Text("Error: ${error.value}")
                } else if (isLoading.value) {
                    CircularProgressIndicator()
                } else {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { context ->
                            WebView(context).apply {
                                settings.apply {
                                    javaScriptEnabled = true
                                    domStorageEnabled = true
                                }

                                webViewClient = GitHubOAuthWebViewClient(
                                    onCodeReceived = { code, state ->
                                        isLoading.value = false
                                        onCodeReceived(code, state)
                                    },
                                    onError = { errorMsg ->
                                        error.value = errorMsg
                                        onError(errorMsg)
                                    }
                                )

                                loadUrl(loginUrl)
                                webViewRef.value = this
                            }
                        }
                    )
                }
            }
        }
    )
}
