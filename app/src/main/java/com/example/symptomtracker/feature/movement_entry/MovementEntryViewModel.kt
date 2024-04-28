package com.example.symptomtracker.feature.movement_entry

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.core.data.repository.MovementRepository
import com.example.symptomtracker.core.database.model.MovementLog
import com.example.symptomtracker.core.model.StoolType
import com.example.symptomtracker.core.ui.DateInputFields
import com.example.symptomtracker.core.ui.DateTimeInput
import com.example.symptomtracker.core.ui.TimeInputFields
import com.example.symptomtracker.core.ui.toDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class MovementEntryViewModel @Inject constructor(private val movementRepository: MovementRepository) :
    ViewModel() {
    var uiState by mutableStateOf(MovementUiState(Calendar.getInstance()))
        private set

    fun updateChosenStoolType(stoolType: StoolType) {
        uiState = uiState.copy(
            chosenStoolType = stoolType
        )
    }

    fun updateDate(dateInputFields: DateInputFields) {
        uiState = uiState.copy(
            dateTimeInput = uiState.dateTimeInput.copy(
                dateInputFields = dateInputFields
            )
        )
    }

    fun updateTime(timeInputFields: TimeInputFields) {
        uiState = uiState.copy(
            dateTimeInput = uiState.dateTimeInput.copy(
                timeInputFields = timeInputFields
            )
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
    val dateTimeInput: DateTimeInput,
    val isEntryValid: Boolean = false,
) {
    constructor(calendar: Calendar) : this(
        dateTimeInput = DateTimeInput(calendar = calendar)
    )
}

fun MovementUiState.toMovementLog(): MovementLog {
    return MovementLog(
        movementLogId = 0,
        date = dateTimeInput.toDate(),
        stoolType = chosenStoolType!!
    )
}
