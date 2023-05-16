package com.example.symptomtracker.ui.food

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.data.food.FoodLogRepository
import com.example.symptomtracker.data.food.FoodLogWithItems
import kotlinx.coroutines.launch

class FoodLogListViewModel(private val foodLogRepository: FoodLogRepository) : ViewModel() {
    var uiState by mutableStateOf(UiState())
        private set

    init {
        viewModelScope.launch {
            foodLogRepository.getAllFoodLogs().collect {
                uiState = uiState.copy(
                    foodLogs = it
                )
            }
        }
    }
}

data class UiState(
    val foodLogs: List<FoodLogWithItems> = listOf(),
)