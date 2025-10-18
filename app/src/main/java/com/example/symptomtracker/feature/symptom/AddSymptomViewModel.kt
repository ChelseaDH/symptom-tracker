package com.example.symptomtracker.feature.symptom

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.core.domain.repository.SymptomRepository
import com.example.symptomtracker.navigation.DATE_ARG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddSymptomViewModel @Inject constructor(
    val symptomRepository: SymptomRepository,
    private val savedStateHandle: SavedStateHandle,
) : AbstractSymptomEntryViewModel(symptomRepository) {
    private val dateArg: String? = savedStateHandle[DATE_ARG]

    init {
        viewModelScope.launch {
            setUiStateWithArgs(date = dateArg)
        }
    }

    override fun submit() {
        if (uiState.isValid()) {
            viewModelScope.launch {
                symptomRepository.insertSymptomLog(uiState.toSymptomLog())
            }
        }
    }
}
