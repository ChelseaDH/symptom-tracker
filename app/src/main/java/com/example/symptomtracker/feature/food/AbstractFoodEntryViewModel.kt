package com.example.symptomtracker.feature.food

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.core.data.repository.FoodLogRepository
import com.example.symptomtracker.core.database.model.FoodLog
import com.example.symptomtracker.core.database.model.FoodLogWithItems
import com.example.symptomtracker.core.database.model.Item
import com.example.symptomtracker.core.ui.DateInputFields
import com.example.symptomtracker.core.ui.DateTimeInput
import com.example.symptomtracker.core.ui.TimeInputFields
import com.example.symptomtracker.core.ui.toDate
import kotlinx.coroutines.launch
import java.util.Calendar

abstract class AbstractFoodEntryViewModel(private val foodLogRepository: FoodLogRepository) :
    ViewModel() {
    var uiState by mutableStateOf(FoodEntryUiState(Calendar.getInstance()))

    private var _selectedItems = listOf<Item>().toMutableStateList()
    private var _allItems = listOf<Item>()

    abstract suspend fun submit()

    init {
        viewModelScope.launch {
            foodLogRepository.getAllItemsStream().collect {
                _allItems = it
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
            _selectedItems.add(uiState.searchState.selectedItem!!)

            uiState = uiState.copy(
                selectedItems = _selectedItems
            )
            clearSearch()
        }
    }

    fun removeItem(item: Item) {
        _selectedItems.remove(item)

        uiState = uiState.copy(
            selectedItems = _selectedItems
        )
    }

    fun updateSelectedSearchItem(item: Item) {
        uiState = uiState.copy(
            searchState = SearchState(
                input = item.name,
                selectedItem = item,
                results = getSearchResults(item.name),
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

            updateSelectedSearchItem(item.copy(itemId = id))
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
        return itemName.isNotBlank() && _allItems.none {
            it.name.equals(
                itemName, ignoreCase = true
            )
        }
    }

    private fun canAddSelectedItemToLog(): Boolean {
        return uiState.searchState.selectedItem != null && !_selectedItems.contains(uiState.searchState.selectedItem!!)
    }

    private fun getSearchResults(itemName: String = uiState.searchState.input): List<Item> {
        return _allItems.filter { item ->
            item.name.contains(itemName, ignoreCase = true)
        }
    }

    protected fun setUiStateWithLog(foodLogWithItems: FoodLogWithItems) {
        uiState = FoodEntryUiState(foodLogWithItems = foodLogWithItems).copy(
            searchState = uiState.searchState.copy(results = _allItems)
        )

        _selectedItems = foodLogWithItems.items.toMutableStateList()
    }
}

data class FoodEntryUiState(
    val selectedItems: List<Item> = listOf(),
    val dateTimeInput: DateTimeInput,
    val searchState: SearchState = SearchState(),
) {
    constructor(calendar: Calendar) : this(
        dateTimeInput = DateTimeInput(calendar = calendar),
    )

    constructor(foodLogWithItems: FoodLogWithItems) : this(
        selectedItems = foodLogWithItems.items,
        dateTimeInput = DateTimeInput(date = foodLogWithItems.getDate()),
    )

    fun isValid(): Boolean =
        selectedItems.isNotEmpty() && !selectedItems.any { item -> item.name.isBlank() }

    fun toFoodLogWithItems(id: Long = 0): FoodLogWithItems = FoodLogWithItems(
        log = FoodLog(foodLogId = id, date = dateTimeInput.toDate()), items = selectedItems
    )
}

data class SearchState(
    val input: String = "",
    val selectedItem: Item? = null,
    val results: List<Item> = listOf(),
    val canCreateNewItem: Boolean = false,
) {
    fun isInputValid(): Boolean = input.isNotEmpty()

    fun toItem(): Item =
        Item(itemId = 0, name = input.trim().replaceFirstChar { it.uppercaseChar() })
}
