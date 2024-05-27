package com.example.symptomtracker.feature.view_food

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.core.data.repository.FoodLogRepository
import com.example.symptomtracker.core.database.model.FoodLogWithItems
import com.example.symptomtracker.core.ui.ViewLogUiState
import com.example.symptomtracker.feature.view_food.navigation.FOOD_LOG_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewFoodViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val foodLogRepository: FoodLogRepository
) : ViewModel() {

    private val logId: Long = checkNotNull(savedStateHandle[FOOD_LOG_ID])

    var uiState by mutableStateOf<ViewFoodUiState>(ViewLogUiState.Loading)
        private set

    init {
        viewModelScope.launch {
            foodLogRepository.getFoodLog(logId).collect { foodLog ->
                uiState = if (foodLog !== null) {
                    ViewLogUiState.Data(foodLog)
                } else {
                    ViewLogUiState.Empty
                }
            }
        }
    }

    fun deleteLog(foodLogWithItems: FoodLogWithItems) {
        viewModelScope.launch {
            foodLogRepository.deleteWithItems(foodLogWithItems)
        }
    }
}

typealias ViewFoodUiState = ViewLogUiState<FoodLogWithItems>