package com.example.gitgauge.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gitgauge.data.model.AnalysisResponse
import com.example.gitgauge.network.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _analysisState = MutableStateFlow<AnalysisState>(AnalysisState.Idle)
    val analysisState: StateFlow<AnalysisState> = _analysisState.asStateFlow()

    private val _analysisResponse = MutableStateFlow<AnalysisResponse?>(null)
    val analysisResponse: StateFlow<AnalysisResponse?> = _analysisResponse.asStateFlow()

    fun analyzeRepository(owner: String, repo: String, ref: String = "main") {
        viewModelScope.launch {
            _analysisState.value = AnalysisState.Loading
            try {
                val response = authRepository.analyzeRepository(owner, repo, ref)
                _analysisResponse.value = response
                _analysisState.value = AnalysisState.Success(response)
            } catch (e: Exception) {
                _analysisState.value = AnalysisState.Error(e.message ?: "Failed to analyze repository")
            }
        }
    }

    fun resetState() {
        _analysisState.value = AnalysisState.Idle
        _analysisResponse.value = null
    }
}

sealed class AnalysisState {
    object Idle : AnalysisState()
    object Loading : AnalysisState()
    data class Success(val response: AnalysisResponse) : AnalysisState()
    data class Error(val message: String) : AnalysisState()
}
