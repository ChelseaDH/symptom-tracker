package com.example.symptomtracker.ui.food

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.data.food.FoodLog
import com.example.symptomtracker.data.food.FoodLogRepository
import com.example.symptomtracker.data.food.FoodLogWithItems
import com.example.symptomtracker.data.food.Item
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*

/**
 * View model to validate and insert Food Logs with Items in to the Room database.
 */
class FoodEntryViewModel(private val foodLogRepository: FoodLogRepository) : ViewModel() {
    var foodLogUiState by mutableStateOf(FoodLogUiState())
        private set

    init {
        viewModelScope.launch {
            foodLogUiState =
                FoodLogUiState(availableItems = foodLogRepository.getAllItemsStream().first())
        }
    }

    private val _chosenItems = listOf<Item>().toMutableStateList()
    val chosenItems: List<Item>
        get() = _chosenItems

    fun addItem() {
        if (foodLogUiState.chosenItem != null && !chosenItems.contains(foodLogUiState.chosenItem!!)) {
            _chosenItems.add(foodLogUiState.chosenItem!!)
        }
    }

    fun removeItem(item: Item) {
        _chosenItems.remove(item)
    }

    fun updateChosenItem(item: Item) {
        foodLogUiState = foodLogUiState.copy(
            chosenItem = item
        )
    }

    suspend fun saveFoodLog() {
        if (validateInput()) {
            foodLogRepository.insertFoodLogWithItems(FoodLogWithItems(
                foodLog = FoodLog(foodLogId = 0, date = Date()),
                items = chosenItems
            ))
        }
    }

    private fun validateInput(): Boolean {
        return chosenItems.isNotEmpty() && !chosenItems.any { item -> item.name.isBlank() }
    }
}

/**
 * Represents the UI state for a Food Log.
 */
data class FoodLogUiState(
    val foodLogDetails: FoodLogDetails = FoodLogDetails(),
    val availableItems: List<Item> = listOf(),
    val chosenItem: Item? = null,
    val isEntryValid: Boolean = false,
)

data class FoodLogDetails(
    val items: List<Item> = listOf(),
)