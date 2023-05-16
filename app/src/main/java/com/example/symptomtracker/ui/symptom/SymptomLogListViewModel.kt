package com.example.symptomtracker.ui.symptom

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.data.symptom.Symptom
import com.example.symptomtracker.data.symptom.SymptomLog
import com.example.symptomtracker.data.symptom.SymptomRepository
import kotlinx.coroutines.launch

class SymptomLogListViewModel(private val symptomRepository: SymptomRepository) : ViewModel() {
    var uiState by mutableStateOf(UiState())
        private set

    init {
        viewModelScope.launch {
            symptomRepository.getAllSymptomLogs().collect {
                uiState = uiState.copy(
                    symptomLogs = it
                )
            }
        }
    }
}

data class UiState(
    val symptomLogs: Map<SymptomLog, List<Symptom>> = mapOf(),
)