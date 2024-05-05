package com.example.symptomtracker.feature.view_movement

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.core.data.repository.MovementRepository
import com.example.symptomtracker.core.database.model.MovementLog
import com.example.symptomtracker.core.ui.ViewLogUiState
import com.example.symptomtracker.feature.view_movement.navigation.MOVEMENT_LOG_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewMovementViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val movementRepository: MovementRepository
) : ViewModel() {

    private val logId: Long = checkNotNull(savedStateHandle[MOVEMENT_LOG_ID])

    var uiState by mutableStateOf<ViewMovementUiState>(ViewLogUiState.Loading)
        private set

    init {
        viewModelScope.launch {
            movementRepository.getMovementLog(logId).collect { log ->
                uiState = ViewLogUiState.Data(log)
            }
        }
    }
}

typealias ViewMovementUiState = ViewLogUiState<MovementLog>
