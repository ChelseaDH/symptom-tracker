package com.example.symptomtracker.feature.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.core.domain.model.Log
import com.example.symptomtracker.core.domain.repository.FoodLogRepository
import com.example.symptomtracker.core.domain.repository.MovementRepository
import com.example.symptomtracker.core.domain.repository.SettingsRepository
import com.example.symptomtracker.core.domain.repository.SymptomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val foodLogRepository: FoodLogRepository,
    private val symptomRepository: SymptomRepository,
    private val movementRepository: MovementRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    private val now = OffsetDateTime.now()
    private val today = now.toLocalDate()

    var uiState by mutableStateOf(UiState(date = today))
        private set

    val mealieIntegrationEnabled: StateFlow<Boolean> =
        settingsRepository.isMealieIntegrationEnabled().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    init {
        viewModelScope.launch {
            updateLogsForDate()
        }
    }

    fun handleEvent(event: HomeScreenEvent) {
        when (event) {
            is HomeScreenEvent.UpdateBottomSheetVisibility -> updateBottomSheetVisibility(visible = event.visible)
            is HomeScreenEvent.GoToPreviousDay -> goToPreviousDay()
            is HomeScreenEvent.GoToNextDay -> goToNextDay()
            is HomeScreenEvent.UpdateDate -> updateDate(date = event.date)
        }
    }

    private fun updateBottomSheetVisibility(visible: Boolean) {
        uiState = uiState.copy(
            showBottomSheet = visible
        )
    }

    private fun goToPreviousDay() {
        updateChosenDate(uiState.date.minusDays(1))
    }

    private fun goToNextDay() {
        if (!uiState.isToday) {
            updateChosenDate(uiState.date.plusDays(1))
        }
    }

    private fun updateDate(date: LocalDate) {
        updateChosenDate(date)
    }

    private fun updateChosenDate(date: LocalDate) {
        viewModelScope.launch {
            uiState = uiState.copy(
                date = date, isToday = date.isEqual(today)
            )
            updateLogsForDate(date)
        }
    }

    private suspend fun updateLogsForDate(date: LocalDate = today) {
        val startDate = OffsetDateTime.of(date.atStartOfDay(), now.offset)
        val endDate = OffsetDateTime.of(date, LocalTime.of(23, 59, 59), now.offset)

        val foodLogs = foodLogRepository.getAllFoodLogsBetweenDates(startDate, endDate)
        val symptomLogs = symptomRepository.getAllSymptomLogsBetweenDates(startDate, endDate)
        val movementLogs = movementRepository.getAllMovementLogsBetweenDates(startDate, endDate)

        combine(foodLogs, symptomLogs, movementLogs) { data1, data2, data3 ->
            data1 + data2 + data3
        }.collect { logs ->
            val sortedLogs = logs.toMutableList()
            sortedLogs.sortBy { it.date }

            uiState = uiState.copy(
                logs = sortedLogs
            )
        }
    }
}

data class UiState(
    val showBottomSheet: Boolean = false,
    val logs: List<Log> = listOf(),
    val isToday: Boolean = true,
    val date: LocalDate,
)

sealed interface HomeScreenEvent {
    data class UpdateBottomSheetVisibility(val visible: Boolean) : HomeScreenEvent
    data object GoToPreviousDay : HomeScreenEvent
    data object GoToNextDay : HomeScreenEvent
    data class UpdateDate(val date: LocalDate) : HomeScreenEvent
}
