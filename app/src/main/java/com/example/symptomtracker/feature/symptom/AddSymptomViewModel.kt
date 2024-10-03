package com.example.symptomtracker.feature.symptom

import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.core.domain.repository.SymptomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddSymptomViewModel @Inject constructor(private val symptomRepository: SymptomRepository) :
    AbstractSymptomEntryViewModel(symptomRepository) {
    override fun submit() {
        if (uiState.isValid()) {
            viewModelScope.launch {
                symptomRepository.insertSymptomLog(uiState.toSymptomLog())
            }
        }
    }
}
