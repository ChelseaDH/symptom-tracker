package com.example.symptomtracker.feature.logs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.core.data.repository.FoodLogRepository
import com.example.symptomtracker.core.data.repository.MovementRepository
import com.example.symptomtracker.core.data.repository.SymptomRepository
import com.example.symptomtracker.core.database.model.FoodLogWithItems
import com.example.symptomtracker.core.database.model.MovementLog
import com.example.symptomtracker.core.model.SymptomLogWithSymptoms
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogsViewModel @Inject constructor(
    private val foodLogRepository: FoodLogRepository,
    private val symptomRepository: SymptomRepository,
    private val movementRepository: MovementRepository,
) : ViewModel() {
    companion object {
        const val FOOD_TAB = "Food"
        const val SYMPTOM_TAB = "Symptom"
        const val MOVEMENT_TAB = "Movement"
    }

    val tabs = listOf(FOOD_TAB, SYMPTOM_TAB, MOVEMENT_TAB)
    var uiState by mutableStateOf(LogsViewUiState())
        private set

    init {
        viewModelScope.launch {
            foodLogRepository.getAllFoodLogs().collect {
                uiState = uiState.copy(
                    tabState = TabUiState.FoodLogs(logs = it)
                )
            }
        }
    }

    fun updateSelectedTab(index: Int) {
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

    fun goToPreviousTab() {
        val newIndex = uiState.selectedTabIndex - 1
        if (newIndex >= 0) {
            updateSelectedTab(newIndex)
        }
    }

    fun goToNextTab() {
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
    object Loading : TabUiState
    data class FoodLogs(val logs: List<FoodLogWithItems>) : TabUiState
    data class SymptomLogs(val logs: List<SymptomLogWithSymptoms>) : TabUiState
    data class MovementLogs(val logs: List<MovementLog>) : TabUiState
}
