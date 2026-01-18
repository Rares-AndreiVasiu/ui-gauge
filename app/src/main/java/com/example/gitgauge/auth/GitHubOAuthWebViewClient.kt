package com.example.gitgauge.auth

import android.webkit.WebView
import android.webkit.WebViewClient
import android.net.Uri
import android.util.Log

class GitHubOAuthWebViewClient(
    private val onCodeReceived: (code: String, state: String?) -> Unit,
    private val onError: (error: String) -> Unit
) : WebViewClient() {

    companion object {
        private const val TAG = "GitHubOAuthWebViewClient"
        const val REDIRECT_URI = "gitgauge://oauth-callback"
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        Log.d(TAG, "URL intercepted: $url")

        return if (url.startsWith(REDIRECT_URI)) {
            // Extract authorization code from callback URL
            try {
                val uri = Uri.parse(url)
                val code = uri.getQueryParameter("code")
                val state = uri.getQueryParameter("state")

                if (code != null) {
                    Log.d(TAG, "Authorization code received: $code")
                    onCodeReceived(code, state)
                } else {
                    val error = uri.getQueryParameter("error") ?: "Unknown error"
                    val errorDescription = uri.getQueryParameter("error_description") ?: ""
                    onError("$error: $errorDescription")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing callback URL", e)
                onError("Failed to parse callback: ${e.message}")
            }
            true
        } else {
            false
        }
    }
}
