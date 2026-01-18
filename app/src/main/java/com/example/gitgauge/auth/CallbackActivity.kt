package com.example.gitgauge.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.gitgauge.MainActivity
import com.example.gitgauge.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CallbackActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Surface(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            LaunchedEffect(Unit) {
                handleCallback()
            }
        }
    }

    private fun handleCallback() {
        val uri = intent.data
        val code = uri?.getQueryParameter("code")
        val state = uri?.getQueryParameter("state")

        if (code != null) {
            authViewModel.handleAuthCallback(code, state)
            // Wait a bit for the auth to complete, then navigate
            window.decorView.postDelayed({
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }, 1500)
        } else {
            // No code received, go back to MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
