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
import kotlinx.coroutines.launch
import java.util.*

/**
 * View model to validate and insert Food Logs with Items in to the Room database.
 */
class FoodEntryViewModel(private val foodLogRepository: FoodLogRepository) : ViewModel() {
    var foodLogUiState by mutableStateOf(FoodLogUiState())
        private set

    private val _chosenItems = listOf<Item>().toMutableStateList()
    private var _allItems = listOf<Item>()

    init {
        viewModelScope.launch {
            foodLogRepository.getAllItemsStream().collect {
                _allItems = it
                foodLogUiState = foodLogUiState.copy(
                    availableItems = getAvailableItems()
                )
            }
        }
    }

    fun addItem() {
        if (foodLogUiState.chosenItem != null && !_chosenItems.contains(foodLogUiState.chosenItem!!)) {
            _chosenItems.add(foodLogUiState.chosenItem!!)

            foodLogUiState = foodLogUiState.copy(
                foodLogDetails = FoodLogDetails(_chosenItems)
            )
            clearItemInputs()
        }
    }

    fun removeItem(item: Item) {
        _chosenItems.remove(item)

        foodLogUiState = foodLogUiState.copy(
            foodLogDetails = FoodLogDetails(_chosenItems)
        )
    }

    fun updateChosenItem(item: Item) {
        foodLogUiState = foodLogUiState.copy(
            chosenItem = item,
            itemName = item.name,
            canCreateNewItemFromInput = false,
            availableItems = getAvailableItems(item.name)
        )
    }

    fun updateItemName(itemName: String) {
        foodLogUiState = foodLogUiState.copy(
            itemName = itemName,
            chosenItem = null,
            canCreateNewItemFromInput = canCreateNewItemFromInput(itemName),
            availableItems = getAvailableItems(itemName)
        )
    }

    fun clearItemInputs() {
        foodLogUiState = foodLogUiState.copy(
            itemName = "",
            chosenItem = null,
            canCreateNewItemFromInput = false,
            availableItems = getAvailableItems("")
        )
    }

    suspend fun insertNewItemFromInput() {
        if (validateNewItemInput()) {
            val item = foodLogUiState.toItem()

            foodLogRepository.insertItem(item)
            updateChosenItem(item)
        }
    }

    suspend fun saveFoodLog() {
        if (validateNewFoodLogInput()) {
            foodLogRepository.insertFoodLogWithItems(foodLogUiState.toFoodLogWithItems())
        }
    }

    private fun validateNewFoodLogInput(): Boolean {
        return _chosenItems.isNotEmpty() && !_chosenItems.any { item -> item.name.isBlank() }
    }

    private fun validateNewItemInput(): Boolean {
        return foodLogUiState.itemName.isNotEmpty()
    }

    private fun canCreateNewItemFromInput(itemName: String): Boolean {
        return itemName.isNotBlank() && _allItems.none {
            it.name.equals(itemName,
                ignoreCase = true)
        }
    }

    private fun getAvailableItems(itemName: String = foodLogUiState.itemName): List<Item> {
        return _allItems.filter { item ->
            item.name.contains(itemName, ignoreCase = true)
        }
    }
}

/**
 * Represents the UI state for a Food Log.
 */
data class FoodLogUiState(
    val foodLogDetails: FoodLogDetails = FoodLogDetails(),
    val availableItems: List<Item> = listOf(),
    val chosenItem: Item? = null,
    val itemName: String = "",
    val isEntryValid: Boolean = false,
    val canCreateNewItemFromInput: Boolean = false,
)

data class FoodLogDetails(
    val items: List<Item> = listOf(),
)

/**
 * Extension function to convert [FoodLogUiState] to [Item].
 */
fun FoodLogUiState.toItem(): Item = Item(
    itemId = 0,
    name = itemName.replaceFirstChar { it.uppercaseChar() }
)

/**
 * Extension function to convert [FoodLogUiState] to [FoodLogWithItems].
 */
fun FoodLogUiState.toFoodLogWithItems(): FoodLogWithItems = FoodLogWithItems(
    foodLog = FoodLog(foodLogId = 0, date = Date()),
    items = foodLogDetails.items
)