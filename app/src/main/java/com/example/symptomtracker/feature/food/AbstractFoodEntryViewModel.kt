package com.example.symptomtracker.feature.food

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.core.data.repository.FoodLogRepository
import com.example.symptomtracker.core.model.FoodItem
import com.example.symptomtracker.core.model.FoodLog
import com.example.symptomtracker.core.ui.DateInputFields
import com.example.symptomtracker.core.ui.DateTimeInput
import com.example.symptomtracker.core.ui.TimeInputFields
import com.example.symptomtracker.core.ui.toDate
import kotlinx.coroutines.launch
import java.util.Calendar

abstract class AbstractFoodEntryViewModel(private val foodLogRepository: FoodLogRepository) :
    ViewModel() {
    var uiState by mutableStateOf(FoodEntryUiState(Calendar.getInstance()))

    private var _selectedFoodItems = listOf<FoodItem>().toMutableStateList()
    private var _allFoodItems = listOf<FoodItem>()

    abstract suspend fun submit()

    init {
        viewModelScope.launch {
            foodLogRepository.getAllItems().collect {
                _allFoodItems = it
                uiState = uiState.copy(
                    searchState = uiState.searchState.copy(
                        results = it
                    )
                )
            }
        }
    }

    fun addItem() {
        if (canAddSelectedItemToLog()) {
            _selectedFoodItems.add(uiState.searchState.selectedItem!!)

            uiState = uiState.copy(
                selectedFoodItems = _selectedFoodItems
            )
            clearSearch()
        }
    }

    fun removeItem(foodItem: FoodItem) {
        _selectedFoodItems.remove(foodItem)

        uiState = uiState.copy(
            selectedFoodItems = _selectedFoodItems
        )
    }

    fun updateSelectedSearchItem(foodItem: FoodItem) {
        uiState = uiState.copy(
            searchState = SearchState(
                input = foodItem.name,
                selectedItem = foodItem,
                results = getSearchResults(foodItem.name),
                canCreateNewItem = false,
            )
        )
    }

    fun updateSearchInput(input: String) {
        uiState = uiState.copy(
            searchState = SearchState(
                input = input,
                selectedItem = null,
                results = getSearchResults(input),
                canCreateNewItem = canCreateNewItemFromInput(input),
            )
        )
    }

    fun clearSearch() {
        uiState = uiState.copy(
            searchState = SearchState(
                input = "",
                selectedItem = null,
                results = getSearchResults(""),
                canCreateNewItem = false,
            )
        )
    }

    suspend fun createNewItemFromInput() {
        if (uiState.searchState.isInputValid()) {
            val item = uiState.searchState.toItem()
            val id = foodLogRepository.insertItem(item)

            updateSelectedSearchItem(item.copy(id = id))
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

    private fun canCreateNewItemFromInput(itemName: String): Boolean {
        return itemName.isNotBlank() && _allFoodItems.none {
            it.name.equals(
                itemName, ignoreCase = true
            )
        }
    }

    private fun canAddSelectedItemToLog(): Boolean {
        return uiState.searchState.selectedItem != null && !_selectedFoodItems.contains(
            uiState.searchState.selectedItem!!
        )
    }

    private fun getSearchResults(itemName: String = uiState.searchState.input): List<FoodItem> {
        return _allFoodItems.filter { item ->
            item.name.contains(itemName, ignoreCase = true)
        }
    }

    protected fun setUiStateWithLog(foodLog: FoodLog) {
        uiState = FoodEntryUiState(foodLog = foodLog).copy(
            searchState = uiState.searchState.copy(results = _allFoodItems)
        )

        _selectedFoodItems = foodLog.items.toMutableStateList()
    }
}

data class FoodEntryUiState(
    val selectedFoodItems: List<FoodItem> = listOf(),
    val dateTimeInput: DateTimeInput,
    val searchState: SearchState = SearchState(),
) {
    constructor(calendar: Calendar) : this(
        dateTimeInput = DateTimeInput(calendar = calendar),
    )

    constructor(foodLog: FoodLog) : this(
        selectedFoodItems = foodLog.items,
        dateTimeInput = DateTimeInput(date = foodLog.date),
    )

    fun isValid(): Boolean =
        selectedFoodItems.isNotEmpty() && !selectedFoodItems.any { item -> item.name.isBlank() }

    fun toFoodLog(id: Long = 0): FoodLog = FoodLog(
        id = id, date = dateTimeInput.toDate(), items = selectedFoodItems
    )
}

data class SearchState(
    val input: String = "",
    val selectedItem: FoodItem? = null,
    val results: List<FoodItem> = listOf(),
    val canCreateNewItem: Boolean = false,
) {
    fun isInputValid(): Boolean = input.isNotEmpty()

    fun toItem(): FoodItem = FoodItem(name = input.trim().replaceFirstChar { it.uppercaseChar() })
}
