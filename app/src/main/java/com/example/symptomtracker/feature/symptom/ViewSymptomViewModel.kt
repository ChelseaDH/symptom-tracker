package com.example.symptomtracker.feature.symptom

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.core.data.repository.SymptomRepository
import com.example.symptomtracker.core.model.SymptomLog
import com.example.symptomtracker.core.ui.ViewLogUiState
import com.example.symptomtracker.feature.symptom.navigation.SYMPTOM_LOG_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewSymptomViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val symptomRepository: SymptomRepository,
) : ViewModel() {
    internal val logId: Long = checkNotNull(savedStateHandle[SYMPTOM_LOG_ID])

    val uiState: StateFlow<ViewSymptomUiState> =
        symptomRepository.getSymptomLogById(logId).map { log ->
            if (log !== null) {
                ViewLogUiState.Data(log)
            } else {
                ViewLogUiState.Empty
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ViewLogUiState.Loading
        )

    fun deleteLog(symptomLog: SymptomLog) {
        viewModelScope.launch {
            symptomRepository.deleteSymptomLog(symptomLog)
        }
    }
}

typealias ViewSymptomUiState = ViewLogUiState<SymptomLog>
