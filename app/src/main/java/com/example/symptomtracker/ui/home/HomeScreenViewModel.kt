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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val foodLogRepository: FoodLogRepository,
    private val symptomRepository: SymptomRepository,
    private val movementRepository: MovementRepository,
) : ViewModel() {
    private var now = OffsetDateTime.now()
    private var today = now.toLocalDate()

    var uiState by mutableStateOf(UiState(date = now))
        private set

    init {
        viewModelScope.launch {
            updateLogsForDate()
        }
    }

    private suspend fun updateLogsForDate(date: OffsetDateTime = OffsetDateTime.now()) {
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

    fun updateBottomSheetVisibility(visible: Boolean) {
        uiState = uiState.copy(
            showBottomSheet = visible
        )
    }

    private fun updateChosenDate(date: OffsetDateTime) {
        viewModelScope.launch {
            uiState = uiState.copy(
                date = date,
                isToday = date.toLocalDate().isEqual(today)
            )
            updateLogsForDate(date)
        }
    }

    fun goToPreviousDay() {
        updateChosenDate(uiState.date.minusDays(1))
    }

    fun goToNextDay() {
        if (!uiState.isToday) {
            updateChosenDate(uiState.date.plusDays(1))
        }
    }

    fun updateDate(m: Long) {
        val date = OffsetDateTime.ofInstant(Instant.ofEpochMilli(m), ZoneId.systemDefault())
        updateChosenDate(date)
    }
}

data class UiState(
    val showBottomSheet: Boolean = false,
    val logs: List<Log> = listOf(),
    val isToday: Boolean = true,
    val date: OffsetDateTime,
)
