package com.example.gitgauge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.gitgauge.ui.screens.AnalysisScreen
import com.example.gitgauge.ui.screens.DashboardScreen
import com.example.gitgauge.ui.screens.LoginScreen
import com.example.gitgauge.ui.screens.SettingsScreen
import com.example.gitgauge.ui.theme.GitgaugeTheme
import com.example.gitgauge.viewmodel.AnalysisViewModel
import com.example.gitgauge.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@Suppress("UnusedMaterial3ScaffoldPaddingParameter")
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private val analysisViewModel: AnalysisViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GitgaugeTheme {
                val isLoggedIn = authViewModel.isLoggedIn.collectAsState()
                val authState = authViewModel.authState.collectAsState()
                val currentScreen = remember { mutableStateOf<Screen>(Screen.Dashboard) }

                Scaffold(modifier = Modifier.fillMaxSize()) {
                    if (isLoggedIn.value && authState.value is com.example.gitgauge.viewmodel.AuthState.Success) {
                        val successState = authState.value as com.example.gitgauge.viewmodel.AuthState.Success

                        when (val screen = currentScreen.value) {
                            is Screen.Dashboard -> {
                                DashboardScreen(
                                    viewModel = authViewModel,
                                    user = successState.user,
                                    onLogout = {
                                        authViewModel.logout()
                                    },
                                    modifier = Modifier.fillMaxSize(),
                                    onRepositoryClick = { owner, repo ->
                                        currentScreen.value = Screen.Analysis(owner, repo, forceReanalysis = false)
                                    },
                                    onSettingsClick = {
                                        currentScreen.value = Screen.Settings
                                    },
                                    onRefreshRepository = { owner, repo ->
                                        analysisViewModel.analyzeRepository(
                                            owner = owner,
                                            repo = repo,
                                            ref = "main",
                                            forceReanalysis = true
                                        )
                                        currentScreen.value = Screen.Analysis(owner, repo, forceReanalysis = true)
                                    }
                                )
                            }
                            is Screen.Settings -> {
                                SettingsScreen(
                                    viewModel = authViewModel,
                                    username = successState.user.login ?: "User",
                                    onBackClick = {
                                        currentScreen.value = Screen.Dashboard
                                    },
                                    onLogout = {
                                        authViewModel.logout()
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            is Screen.Analysis -> {
                                AnalysisScreen(
                                    viewModel = analysisViewModel,
                                    owner = screen.owner,
                                    repo = screen.repo,
                                    forceReanalysis = screen.forceReanalysis,
                                    onBackClick = {
                                        currentScreen.value = Screen.Dashboard
                                        analysisViewModel.resetState()
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
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

sealed class Screen {
    object Dashboard : Screen()
    object Settings : Screen()
    data class Analysis(val owner: String, val repo: String, val forceReanalysis: Boolean = false) : Screen()
}
