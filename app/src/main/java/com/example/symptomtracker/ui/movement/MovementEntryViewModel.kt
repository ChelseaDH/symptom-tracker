package com.example.symptomtracker.ui.movement

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.data.movement.MovementLog
import com.example.symptomtracker.data.movement.MovementRepository
import com.example.symptomtracker.data.movement.StoolType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class MovementEntryViewModel(private val movementRepository: MovementRepository) : ViewModel() {
    var uiState by mutableStateOf(MovementUiState())
        private set

    fun updateChosenStoolType(stoolType: StoolType) {
        uiState = uiState.copy(
            chosenStoolType = stoolType
        )
    }

    fun insertMovementLog() {
        if (validateInsertMovementLog()) {
            viewModelScope.launch(Dispatchers.IO) {
                movementRepository.insertMovementLog(uiState.toMovementLog())
            }
        }
    }

    private fun validateInsertMovementLog(uiState: MovementUiState = this.uiState): Boolean {
        return uiState.chosenStoolType != null
    }
}

data class MovementUiState(
    val chosenStoolType: StoolType? = null,
    val isEntryValid: Boolean = false,
)

fun MovementUiState.toMovementLog(): MovementLog {
    return MovementLog(
        symptomLogId = 0,
        date = Date(),
        stoolType = chosenStoolType!!
    )
}
