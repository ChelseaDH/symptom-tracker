package com.example.symptomtracker.feature.logs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.core.domain.model.FoodLog
import com.example.symptomtracker.core.domain.model.MovementLog
import com.example.symptomtracker.core.domain.model.SymptomLog
import com.example.symptomtracker.core.domain.repository.FoodLogRepository
import com.example.symptomtracker.core.domain.repository.MovementRepository
import com.example.symptomtracker.core.domain.repository.SettingsRepository
import com.example.symptomtracker.core.domain.repository.SymptomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogsViewModel @Inject constructor(
    private val foodLogRepository: FoodLogRepository,
    private val symptomRepository: SymptomRepository,
    private val movementRepository: MovementRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    companion object {
        const val FOOD_TAB = "Food"
        const val SYMPTOM_TAB = "Symptom"
        const val MOVEMENT_TAB = "Movement"
    }

    val tabs = listOf(FOOD_TAB, SYMPTOM_TAB, MOVEMENT_TAB)
    var uiState by mutableStateOf(LogsViewUiState())
        private set

    val mealieIntegrationEnabled: StateFlow<Boolean> =
        settingsRepository.isMealieIntegrationEnabled().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    init {
        viewModelScope.launch {
            foodLogRepository.getAllFoodLogs().collect {
                uiState = uiState.copy(
                    tabState = TabUiState.FoodLogs(logs = it)
                )
            }
        }
    }

    fun handleEvent(event: LogsViewEvent) {
        when (event) {
            is LogsViewEvent.UpdateSelectedTab -> updateSelectedTab(index = event.index)
            LogsViewEvent.GoToPreviousTab -> goToPreviousTab()
            LogsViewEvent.GoToNextTab -> goToNextTab()
        }
    }

    private fun updateSelectedTab(index: Int) {
        viewModelScope.launch {
            when (tabs[index]) {
                FOOD_TAB -> foodLogRepository.getAllFoodLogs().collect {
                    uiState = uiState.copy(
                        selectedTabIndex = index,
                        tabState = TabUiState.FoodLogs(logs = it)
                    )
                }

                SYMPTOM_TAB -> symptomRepository.getAllSymptomLogs().collect {
                    uiState = uiState.copy(
                        selectedTabIndex = index,
                        tabState = TabUiState.SymptomLogs(logs = it)
                    )
                }

                MOVEMENT_TAB -> movementRepository.getAllMovementLogs().collect {
                    uiState = uiState.copy(
                        selectedTabIndex = index,
                        tabState = TabUiState.MovementLogs(logs = it)
                    )
                }

            }
        }
    }

    private fun goToPreviousTab() {
        val newIndex = uiState.selectedTabIndex - 1
        if (newIndex >= 0) {
            updateSelectedTab(newIndex)
        }
    }

    private fun goToNextTab() {
        val newIndex = uiState.selectedTabIndex + 1
        if (newIndex < tabs.size) {
            updateSelectedTab(newIndex)
        }
    }
}

data class LogsViewUiState(
    val selectedTabIndex: Int = 0,
    val tabState: TabUiState = TabUiState.Loading,
)

sealed interface TabUiState {
    data object Loading : TabUiState
    data class FoodLogs(val logs: List<FoodLog>) : TabUiState
    data class SymptomLogs(val logs: List<SymptomLog>) : TabUiState
    data class MovementLogs(val logs: List<MovementLog>) : TabUiState
}

sealed interface LogsViewEvent {
    data class UpdateSelectedTab(val index: Int) : LogsViewEvent
    data object GoToPreviousTab : LogsViewEvent
    data object GoToNextTab : LogsViewEvent
}
