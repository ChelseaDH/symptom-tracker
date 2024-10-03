package com.example.symptomtracker.feature.food

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.core.domain.model.FoodLog
import com.example.symptomtracker.core.domain.repository.FoodLogRepository
import com.example.symptomtracker.core.ui.ViewLogUiState
import com.example.symptomtracker.feature.food.navigation.FOOD_LOG_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewFoodViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle, private val foodLogRepository: FoodLogRepository
) : ViewModel() {
    internal val logId: Long = checkNotNull(savedStateHandle[FOOD_LOG_ID])

    val uiState: StateFlow<ViewFoodUiState> =
        foodLogRepository.getFoodLog(logId).map { log ->
            if (log !== null) {
                ViewLogUiState.Data(log)
            } else {
                ViewLogUiState.Empty
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ViewLogUiState.Loading
        )

    fun handleEvent(event: ViewFoodEvent) {
        when (event) {
            is ViewFoodEvent.DeleteLog -> deleteLog(foodLog = event.foodLog)
        }
    }

    private fun deleteLog(foodLog: FoodLog) {
        viewModelScope.launch {
            foodLogRepository.deleteFoodLog(foodLog)
        }
    }
}

typealias ViewFoodUiState = ViewLogUiState<FoodLog>

sealed interface ViewFoodEvent {
    data class DeleteLog(val foodLog: FoodLog) : ViewFoodEvent
}
