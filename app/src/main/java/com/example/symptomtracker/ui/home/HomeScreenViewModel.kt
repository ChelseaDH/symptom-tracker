package com.example.symptomtracker.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.data.Log
import com.example.symptomtracker.data.food.FoodLogRepository
import com.example.symptomtracker.data.movement.MovementRepository
import com.example.symptomtracker.data.symptom.SymptomRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.OffsetDateTime

class HomeScreenViewModel(
    private val foodLogRepository: FoodLogRepository,
    private val symptomRepository: SymptomRepository,
    private val movementRepository: MovementRepository,
) : ViewModel() {
    var uiState by mutableStateOf(UiState())
        private set

    init {
        viewModelScope.launch {
            val date = OffsetDateTime.now()
            val startDate = date.withHour(0).withMinute(0).withSecond(0)
            val endDate = date.withHour(23).withMinute(59).withSecond(59)

            val foodLogs = foodLogRepository.getAllFoodLogsBetweenDates(startDate, endDate)
            val symptomLogs = symptomRepository.getAllSymptomLogsBetweenDates(startDate, endDate)
            val movementLogs = movementRepository.getAllMovementLogsBetweenDates(startDate, endDate)

            combine(foodLogs, symptomLogs, movementLogs) { data1, data2, data3 ->
                data1 + data2 + data3
            }.collect { logs ->
                val sortedLogs = logs.toMutableList()
                sortedLogs.sortBy { it.getDate() }

                uiState = uiState.copy(
                    logs = sortedLogs
                )
            }
        }
    }

    fun updateBottomSheetVisibility(visible: Boolean) {
        uiState = uiState.copy(
            showBottomSheet = visible
        )
    }
}

data class UiState(
    val showBottomSheet: Boolean = false,
    val logs: List<Log> = listOf(),
)
