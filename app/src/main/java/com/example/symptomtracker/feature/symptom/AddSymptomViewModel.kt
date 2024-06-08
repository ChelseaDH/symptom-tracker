package com.example.symptomtracker.feature.symptom

import com.example.symptomtracker.core.data.repository.SymptomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddSymptomViewModel @Inject constructor(private val symptomRepository: SymptomRepository) :
    AbstractSymptomEntryViewModel(symptomRepository) {
    override suspend fun submit() {
        if (uiState.isValid()) {
            symptomRepository.insertSymptomLogWithSymptom(uiState.toSymptomLogWithSymptoms())
        }
    }
}
