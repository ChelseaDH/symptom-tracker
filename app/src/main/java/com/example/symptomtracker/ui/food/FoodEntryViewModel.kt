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
import com.example.symptomtracker.ui.components.DateInputFields
import com.example.symptomtracker.ui.components.DateTimeInput
import com.example.symptomtracker.ui.components.TimeInputFields
import com.example.symptomtracker.ui.components.toDate
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * View model to validate and insert Food Logs with Items in to the Room database.
 */
class FoodEntryViewModel(private val foodLogRepository: FoodLogRepository) : ViewModel() {
    var uiState by mutableStateOf(FoodLogUiState(calendar = Calendar.getInstance()))
        private set

    private val _chosenItems = listOf<Item>().toMutableStateList()
    private var _allItems = listOf<Item>()

    init {
        viewModelScope.launch {
            foodLogRepository.getAllItemsStream().collect {
                _allItems = it
                uiState = uiState.copy(
                    availableItems = getAvailableItems()
                )
            }
        }
    }

    fun addItem() {
        if (uiState.chosenItem != null && !_chosenItems.contains(uiState.chosenItem!!)) {
            _chosenItems.add(uiState.chosenItem!!)

            uiState = uiState.copy(
                foodLogDetails = FoodLogDetails(_chosenItems)
            )
            clearItemInputs()
        }
    }

    fun removeItem(item: Item) {
        _chosenItems.remove(item)

        uiState = uiState.copy(
            foodLogDetails = FoodLogDetails(_chosenItems)
        )
    }

    fun updateChosenItem(item: Item) {
        uiState = uiState.copy(
            chosenItem = item,
            itemName = item.name,
            canCreateNewItemFromInput = false,
            availableItems = getAvailableItems(item.name)
        )
    }

    fun updateItemName(itemName: String) {
        uiState = uiState.copy(
            itemName = itemName,
            chosenItem = null,
            canCreateNewItemFromInput = canCreateNewItemFromInput(itemName),
            availableItems = getAvailableItems(itemName)
        )
    }

    fun clearItemInputs() {
        uiState = uiState.copy(
            itemName = "",
            chosenItem = null,
            canCreateNewItemFromInput = false,
            availableItems = getAvailableItems("")
        )
    }

    suspend fun insertNewItemFromInput() {
        if (validateNewItemInput()) {
            val item = uiState.toItem()

            foodLogRepository.insertItem(item)
            updateChosenItem(item)
        }
    }

    suspend fun saveFoodLog() {
        if (validateNewFoodLogInput()) {
            foodLogRepository.insertFoodLogWithItems(uiState.toFoodLogWithItems())
        }
    }

    fun updateDate(dateInputFields: DateInputFields) {
        uiState = uiState.copy(
            dateTimeInput = uiState.dateTimeInput.copy(
                dateInputFields = dateInputFields
            )
        )
    }

    fun updateTime(timeInputFields: TimeInputFields) {
        uiState = uiState.copy(
            dateTimeInput = uiState.dateTimeInput.copy(
                timeInputFields = timeInputFields
            )
        )
    }

    private fun validateNewFoodLogInput(): Boolean {
        return _chosenItems.isNotEmpty() && !_chosenItems.any { item -> item.name.isBlank() }
    }

    private fun validateNewItemInput(): Boolean {
        return uiState.itemName.isNotEmpty()
    }

    private fun canCreateNewItemFromInput(itemName: String): Boolean {
        return itemName.isNotBlank() && _allItems.none {
            it.name.equals(
                itemName,
                ignoreCase = true
            )
        }
    }

    private fun getAvailableItems(itemName: String = uiState.itemName): List<Item> {
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
    val dateTimeInput: DateTimeInput,
    val isEntryValid: Boolean = false,
    val canCreateNewItemFromInput: Boolean = false,
) {
    constructor(calendar: Calendar) : this(
        dateTimeInput = DateTimeInput(calendar = calendar),
    )
}

data class FoodLogDetails(
    val items: List<Item> = listOf(),
)

/**
 * Extension function to convert [FoodLogUiState] to [Item].
 */
fun FoodLogUiState.toItem(): Item = Item(
    itemId = 0,
    name = itemName.trim().replaceFirstChar { it.uppercaseChar() }
)

/**
 * Extension function to convert [FoodLogUiState] to [FoodLogWithItems].
 */
fun FoodLogUiState.toFoodLogWithItems(): FoodLogWithItems = FoodLogWithItems(
    log = FoodLog(foodLogId = 0, date = dateTimeInput.toDate()),
    items = foodLogDetails.items
)
