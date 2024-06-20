package com.example.symptomtracker.feature.movement

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.core.data.repository.MovementRepository
import com.example.symptomtracker.feature.movement.navigation.MOVEMENT_LOG_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditMovementViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val movementRepository: MovementRepository
) : AbstractMovementEntryViewModel() {
    internal val logId: Long = checkNotNull(savedStateHandle[MOVEMENT_LOG_ID])

    init {
        viewModelScope.launch {
            movementRepository.getMovementLogById(logId).collect { log ->
                if (log !== null) {
                    updateFieldsWithLog(movementLog = log)
                }
            }
        }
    }

    override fun submit() {
        if (uiState.isValid()) {
            viewModelScope.launch {
                movementRepository.updateLog(uiState.toMovementLog(logId))
            }
        }
    }
}
