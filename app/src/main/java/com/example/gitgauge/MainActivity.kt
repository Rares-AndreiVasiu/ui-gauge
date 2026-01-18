package com.example.gitgauge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.example.gitgauge.ui.screens.DashboardScreen
import com.example.gitgauge.ui.screens.LoginScreen
import com.example.gitgauge.ui.theme.GitgaugeTheme
import com.example.gitgauge.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GitgaugeTheme {
                val isLoggedIn = authViewModel.isLoggedIn.collectAsState()
                val authState = authViewModel.authState.collectAsState()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (isLoggedIn.value && authState.value is com.example.gitgauge.viewmodel.AuthState.Success) {
                        val successState = authState.value as com.example.gitgauge.viewmodel.AuthState.Success
                        DashboardScreen(
                            viewModel = authViewModel,
                            user = successState.user,
                            onLogout = {
                                authViewModel.logout()
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        LoginScreen(
                            viewModel = authViewModel,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}