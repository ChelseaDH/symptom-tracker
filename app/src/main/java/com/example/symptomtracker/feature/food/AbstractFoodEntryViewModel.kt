package com.example.symptomtracker.feature.food

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.core.designsystem.component.DateTimeInput
import com.example.symptomtracker.core.designsystem.component.TextInput
import com.example.symptomtracker.core.designsystem.component.TextValidationError
import com.example.symptomtracker.core.domain.model.FoodItem
import com.example.symptomtracker.core.domain.model.FoodLog
import com.example.symptomtracker.core.domain.repository.FoodLogRepository
import com.example.symptomtracker.core.util.getPrioritisedSearchResults
import com.example.symptomtracker.core.util.toFoodItemName
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

abstract class AbstractFoodEntryViewModel(private val foodLogRepository: FoodLogRepository) :
    ViewModel() {
    var uiState by mutableStateOf(FoodEntryUiState())

    private var _selectedFoodItems = listOf<FoodItem>().toMutableStateList()
    private var _allFoodItems = listOf<FoodItem>()

    abstract fun submit()

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

    fun handleEvent(event: FoodEntryEvent) {
        when (event) {
            is FoodEntryEvent.AddItem -> addItem()
            is FoodEntryEvent.RemoveItem -> removeItem(foodItem = event.foodItem)
            is FoodEntryEvent.UpdateSelectedSearchItem -> updateSelectedSearchItem(foodItem = event.foodItem)
            is FoodEntryEvent.UpdateSearchInput -> updateSearchInput(input = event.input)
            is FoodEntryEvent.ClearSearch -> clearSearch()
            is FoodEntryEvent.CreateNewItemFromInput -> createNewItemFromInput()
            is FoodEntryEvent.UpdateDate -> updateDate(date = event.date)
            is FoodEntryEvent.UpdateTime -> updateTime(time = event.time)
            is FoodEntryEvent.Submit -> submit()
        }
    }

    private fun addItem() {
        if (canAddSelectedItemToLog()) {
            _selectedFoodItems.add(uiState.searchState.selectedItem!!)

            uiState = uiState.copy(
                selectedFoodItems = _selectedFoodItems
            )
            clearSearch()
        }
    }

    private fun removeItem(foodItem: FoodItem) {
        _selectedFoodItems.remove(foodItem)

        uiState = uiState.copy(
            selectedFoodItems = _selectedFoodItems
        )
    }

    private fun updateSelectedSearchItem(foodItem: FoodItem) {
        uiState = uiState.copy(
            searchState = SearchState(
                input = TextInput(value = foodItem.name),
                selectedItem = foodItem,
                results = getSearchResults(foodItem.name),
                canCreateNewItem = false,
            )
        )
    }

    private fun updateSearchInput(input: String) {
        uiState = uiState.copy(
            searchState = SearchState(
                input = TextInput(value = input),
                selectedItem = null,
                results = getSearchResults(input),
                canCreateNewItem = canCreateNewItemFromInput(input),
            )
        )
    }

    private fun clearSearch() {
        uiState = uiState.copy(
            searchState = SearchState(
                input = TextInput(),
                selectedItem = null,
                results = getSearchResults(""),
                canCreateNewItem = false,
            )
        )
    }

    private fun createNewItemFromInput() {
        val validationError =
            uiState.searchState.input.findValidationError(errors = listOf(TextValidationError.BLANK))
        if (validationError == null) {
            viewModelScope.launch {
                val item = uiState.searchState.toItem()
                val id = foodLogRepository.insertItem(item)

                updateSelectedSearchItem(item.copy(id = id))
            }
        } else {
            uiState = uiState.copy(
                searchState = uiState.searchState.copy(
                    input = uiState.searchState.input.copy(
                        validationError = validationError
                    )
                )
            )
        }
    }

    private fun updateDate(date: LocalDate) {
        uiState = uiState.copy(
            dateTimeInput = uiState.dateTimeInput.copy(
                date = date
            )
        )
    }

    private fun updateTime(time: LocalTime) {
        uiState = uiState.copy(
            dateTimeInput = uiState.dateTimeInput.copy(
                time = time
            )
        )
    }

    private fun canCreateNewItemFromInput(itemName: String): Boolean {
        return itemName.isNotBlank() && _allFoodItems.none {
            it.name.equals(itemName.trim(), ignoreCase = true)
        }
    }

    private fun canAddSelectedItemToLog(): Boolean {
        return uiState.searchState.selectedItem != null && !_selectedFoodItems.contains(
            uiState.searchState.selectedItem!!
        )
    }

    private fun getSearchResults(itemName: String = uiState.searchState.input.value): List<FoodItem> =
        getPrioritisedSearchResults(
            searchTerm = itemName,
            items = _allFoodItems,
            getItemComparisonString = { it.name },
        )

    protected fun setUiStateWithLog(foodLog: FoodLog) {
        uiState = FoodEntryUiState(foodLog = foodLog).copy(
            searchState = uiState.searchState.copy(results = _allFoodItems)
        )

        _selectedFoodItems = foodLog.items.toMutableStateList()
    }

    protected suspend fun setUiStateFromPrefill(items: List<String>? = null, date: String? = null) {
        items?.let {
            _selectedFoodItems = items
                .distinct()
                .filter { it.isNotBlank() }
                .map { item ->
                    foodLogRepository.insertOrGetItemByName(item.toFoodItemName())
                }
                .toMutableStateList()
        }

        val updatedDateTimeInput = date?.let {
            uiState.dateTimeInput.copy(date = LocalDate.parse(it))
        } ?: uiState.dateTimeInput

        uiState = uiState.copy(
            selectedFoodItems = _selectedFoodItems,
            dateTimeInput = updatedDateTimeInput,
        )
    }
}

data class FoodEntryUiState(
    val selectedFoodItems: List<FoodItem> = listOf(),
    val dateTimeInput: DateTimeInput = DateTimeInput(),
    val searchState: SearchState = SearchState(),
) {
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
    val input: TextInput = TextInput(),
    val selectedItem: FoodItem? = null,
    val results: List<FoodItem> = listOf(),
    val canCreateNewItem: Boolean = false,
) {
    fun toItem(): FoodItem = FoodItem(name = input.value.toFoodItemName())
}

sealed interface FoodEntryEvent {
    data object AddItem : FoodEntryEvent
    data class RemoveItem(val foodItem: FoodItem) : FoodEntryEvent
    data class UpdateSelectedSearchItem(val foodItem: FoodItem) : FoodEntryEvent
    data class UpdateSearchInput(val input: String) : FoodEntryEvent
    data object ClearSearch : FoodEntryEvent
    data object CreateNewItemFromInput : FoodEntryEvent
    data class UpdateDate(val date: LocalDate) : FoodEntryEvent
    data class UpdateTime(val time: LocalTime) : FoodEntryEvent
    data object Submit : FoodEntryEvent
}
