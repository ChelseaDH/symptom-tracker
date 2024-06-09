package com.example.symptomtracker.feature.movement

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.symptomtracker.core.model.MovementLog
import com.example.symptomtracker.core.model.StoolType
import com.example.symptomtracker.core.ui.DateInputFields
import com.example.symptomtracker.core.ui.DateTimeInput
import com.example.symptomtracker.core.ui.TimeInputFields
import java.util.Calendar

abstract class AbstractMovementEntryViewModel : ViewModel() {
    var uiState by mutableStateOf(MovementEntryUiState(Calendar.getInstance()))
        private set

    abstract fun submit()

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

    protected fun updateFieldsWithLog(movementLog: MovementLog) {
        uiState = MovementEntryUiState(movementLog = movementLog)
    }
}

data class MovementEntryUiState(
    val chosenStoolType: StoolType? = null,
    val dateTimeInput: DateTimeInput,
) {
    constructor(calendar: Calendar) : this(
        dateTimeInput = DateTimeInput(calendar = calendar)
    )

    constructor(movementLog: MovementLog) : this(
        chosenStoolType = movementLog.stoolType,
        dateTimeInput = DateTimeInput(date = movementLog.date)
    )

    fun isValid(): Boolean {
        return this.chosenStoolType !== null
    }
}
