package com.example.symptomtracker.feature.view_symptom

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.core.data.repository.SymptomRepository
import com.example.symptomtracker.core.database.model.SymptomLogWithSymptoms
import com.example.symptomtracker.core.ui.ViewLogUiState
import com.example.symptomtracker.feature.view_symptom.navigation.SYMPTOM_LOG_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewSymptomViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val symptomRepository: SymptomRepository,
) : ViewModel() {
    private val logId: Long = checkNotNull(savedStateHandle[SYMPTOM_LOG_ID])

    var uiState by mutableStateOf<ViewSymptomUiState>(ViewLogUiState.Loading)
        private set

    init {
        viewModelScope.launch {
            symptomRepository.getSymptomLog(logId).collect { symptomLog ->
                uiState = if (symptomLog !== null) {
                    ViewLogUiState.Data(symptomLog)
                } else {
                    ViewLogUiState.Empty
                }
            }
        }
    }

    fun deleteLog(symptomLogWithSymptoms: SymptomLogWithSymptoms) {
        viewModelScope.launch {
            symptomRepository.deleteWithSymptoms(symptomLogWithSymptoms)
        }
    }
}

typealias ViewSymptomUiState = ViewLogUiState<SymptomLogWithSymptoms>