package com.example.symptomtracker.feature.movement

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.symptomtracker.core.designsystem.component.DateTimeInput
import com.example.symptomtracker.core.domain.model.MovementLog
import com.example.symptomtracker.core.domain.model.StoolType
import java.time.LocalDate
import java.time.LocalTime

abstract class AbstractMovementEntryViewModel : ViewModel() {
    var uiState by mutableStateOf(MovementEntryUiState())
        private set

    abstract fun submit()

    fun updateChosenStoolType(stoolType: StoolType) {
        uiState = uiState.copy(
            chosenStoolType = stoolType
        )
    }

    fun updateDate(date: LocalDate) {
        uiState = uiState.copy(
            dateTimeInput = uiState.dateTimeInput.copy(
                date = date
            )
        )
    }

    fun updateTime(time: LocalTime) {
        uiState = uiState.copy(
            dateTimeInput = uiState.dateTimeInput.copy(
                time = time
            )
        )
    }

    protected fun updateFieldsWithLog(movementLog: MovementLog) {
        uiState = MovementEntryUiState(movementLog = movementLog)
    }
}

data class MovementEntryUiState(
    val chosenStoolType: StoolType? = null,
    val dateTimeInput: DateTimeInput = DateTimeInput(),
) {
    constructor(movementLog: MovementLog) : this(
        chosenStoolType = movementLog.stoolType,
        dateTimeInput = DateTimeInput(date = movementLog.date)
    )

    fun isValid(): Boolean {
        return this.chosenStoolType !== null
    }
}
