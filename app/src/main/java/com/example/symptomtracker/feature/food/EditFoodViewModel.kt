package com.example.symptomtracker.feature.food

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.core.data.repository.FoodLogRepository
import com.example.symptomtracker.feature.food.navigation.FOOD_LOG_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditFoodViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val foodLogRepository: FoodLogRepository,
) : AbstractFoodEntryViewModel(foodLogRepository) {
    internal val logId: Long = checkNotNull(savedStateHandle[FOOD_LOG_ID])

    init {
        viewModelScope.launch {
            foodLogRepository.getFoodLog(logId).collect { log ->
                if (log !== null) {
                    setUiStateWithLog(log)
                }
            }
        }
    }

    override suspend fun submit() {
        if (uiState.isValid()) {
            foodLogRepository.updateFoodLog(uiState.toFoodLog(logId))
        }
    }
}
