package com.example.symptomtracker.feature.movement

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.core.domain.model.MovementLog
import com.example.symptomtracker.core.domain.repository.MovementRepository
import com.example.symptomtracker.core.ui.ViewLogUiState
import com.example.symptomtracker.feature.movement.navigation.MOVEMENT_LOG_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewMovementViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val movementRepository: MovementRepository
) : ViewModel() {
    internal val logId: Long = checkNotNull(savedStateHandle[MOVEMENT_LOG_ID])

    val uiState: StateFlow<ViewMovementUiState> =
        movementRepository.getMovementLogById(logId).map { log ->
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

    fun handleEvent(event: ViewMovementEvent) {
        when (event) {
            is ViewMovementEvent.DeleteLog -> deleteLog(movementLog = event.movementLog)
        }
    }

    private fun deleteLog(movementLog: MovementLog) {
        viewModelScope.launch {
            movementRepository.deleteLog(movementLog)
        }
    }
}

typealias ViewMovementUiState = ViewLogUiState<MovementLog>

sealed interface ViewMovementEvent {
    data class DeleteLog(val movementLog: MovementLog) : ViewMovementEvent
}
