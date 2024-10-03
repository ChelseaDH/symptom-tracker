package com.example.symptomtracker.feature.symptom

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.core.domain.repository.SymptomRepository
import com.example.symptomtracker.feature.symptom.navigation.SYMPTOM_LOG_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditSymptomViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val symptomRepository: SymptomRepository,
) : AbstractSymptomEntryViewModel(symptomRepository) {
    internal val logId: Long = checkNotNull(savedStateHandle[SYMPTOM_LOG_ID])

    init {
        viewModelScope.launch {
            symptomRepository.getSymptomLogById(logId).collect { symptomLog ->
                if (symptomLog !== null) {
                    setUiStateWithLog(symptomLog)
                }
            }
        }
    }

    override fun submit() {
        if (uiState.isValid()) {
            viewModelScope.launch {
                symptomRepository.updateSymptomLog(uiState.toSymptomLog(logId))
            }
        }
    }
}
