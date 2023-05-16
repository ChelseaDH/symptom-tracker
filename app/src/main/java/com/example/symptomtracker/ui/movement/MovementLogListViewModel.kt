package com.example.symptomtracker.ui.movement

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.data.movement.MovementLog
import com.example.symptomtracker.data.movement.MovementRepository
import kotlinx.coroutines.launch

class MovementLogListViewModel(private val movementRepository: MovementRepository) : ViewModel() {
    var uiState by mutableStateOf(UiState())
        private set

    init {
        viewModelScope.launch {
            movementRepository.getAllMovementLogs().collect {
                uiState = uiState.copy(
                    movementLogs = it
                )
            }
        }
    }
}

data class UiState(
    val movementLogs: List<MovementLog> = listOf(),
)