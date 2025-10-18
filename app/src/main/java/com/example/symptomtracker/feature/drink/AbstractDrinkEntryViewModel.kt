package com.example.symptomtracker.feature.drink

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.core.designsystem.component.DateTimeInput
import com.example.symptomtracker.core.designsystem.component.TextInput
import com.example.symptomtracker.core.designsystem.component.TextValidationError
import com.example.symptomtracker.core.domain.model.DrinkItem
import com.example.symptomtracker.core.domain.model.DrinkLog
import com.example.symptomtracker.core.domain.repository.DrinkLogRepository
import com.example.symptomtracker.core.util.getPrioritisedSearchResults
import com.example.symptomtracker.core.util.toDrinkItemName
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

abstract class AbstractDrinkEntryViewModel(private val drinkLogRepository: DrinkLogRepository) :
    ViewModel() {
    var uiState by mutableStateOf(DrinkEntryUiState())

    private var _selectedDrinkItems = listOf<DrinkItem>().toMutableStateList()
    private var _allDrinkItems = listOf<DrinkItem>()

    abstract fun submit()

    init {
        viewModelScope.launch {
            drinkLogRepository.getAllItems().collect {
                _allDrinkItems = it
                uiState = uiState.copy(
                    searchState = uiState.searchState.copy(
                        results = it
                    )
                )
            }
        }
    }

    fun handleEvent(event: DrinkEntryEvent) {
        when (event) {
            is DrinkEntryEvent.AddItem -> addItem()
            is DrinkEntryEvent.RemoveItem -> removeItem(drinkItem = event.drinkItem)
            is DrinkEntryEvent.UpdateSelectedSearchItem -> updateSelectedSearchItem(drinkItem = event.drinkItem)
            is DrinkEntryEvent.UpdateSearchInput -> updateSearchInput(input = event.input)
            is DrinkEntryEvent.ClearSearch -> clearSearch()
            is DrinkEntryEvent.CreateNewItemFromInput -> createNewItemFromInput()
            is DrinkEntryEvent.UpdateDate -> updateDate(date = event.date)
            is DrinkEntryEvent.UpdateTime -> updateTime(time = event.time)
            is DrinkEntryEvent.Submit -> submit()
        }
    }

    private fun addItem() {
        if (canAddSelectedItemToLog()) {
            _selectedDrinkItems.add(uiState.searchState.selectedItem!!)

            uiState = uiState.copy(
                selectedDrinkItems = _selectedDrinkItems
            )
            clearSearch()
        }
    }

    private fun removeItem(drinkItem: DrinkItem) {
        _selectedDrinkItems.remove(drinkItem)

        uiState = uiState.copy(
            selectedDrinkItems = _selectedDrinkItems
        )
    }

    private fun updateSelectedSearchItem(drinkItem: DrinkItem) {
        uiState = uiState.copy(
            searchState = SearchState(
                input = TextInput(value = drinkItem.name),
                selectedItem = drinkItem,
                results = getSearchResults(drinkItem.name),
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
                val id = drinkLogRepository.insertItem(item)

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
        return itemName.isNotBlank() && _allDrinkItems.none {
            it.name.equals(itemName.trim(), ignoreCase = true)
        }
    }

    private fun canAddSelectedItemToLog(): Boolean {
        return uiState.searchState.selectedItem != null && !_selectedDrinkItems.contains(
            uiState.searchState.selectedItem!!
        )
    }

    private fun getSearchResults(itemName: String = uiState.searchState.input.value): List<DrinkItem> =
        getPrioritisedSearchResults(
            searchTerm = itemName,
            items = _allDrinkItems,
            getItemComparisonString = { it.name },
        )

    protected fun setUiStateWithLog(drinkLog: DrinkLog) {
        uiState = DrinkEntryUiState(drinkLog = drinkLog).copy(
            searchState = uiState.searchState.copy(results = _allDrinkItems)
        )

        _selectedDrinkItems = drinkLog.items.toMutableStateList()
    }

    protected suspend fun setUiStateFromPrefill(items: List<String>? = null, date: String? = null) {
        items?.let {
            _selectedDrinkItems = items.distinct().filter { it.isNotBlank() }.map { item ->
                drinkLogRepository.insertOrGetItemByName(item.toDrinkItemName())
            }.toMutableStateList()
        }

        val updatedDateTimeInput = date?.let {
            uiState.dateTimeInput.copy(date = LocalDate.parse(it))
        } ?: uiState.dateTimeInput

        uiState = uiState.copy(
            selectedDrinkItems = _selectedDrinkItems,
            dateTimeInput = updatedDateTimeInput,
        )
    }
}

data class DrinkEntryUiState(
    val selectedDrinkItems: List<DrinkItem> = listOf(),
    val dateTimeInput: DateTimeInput = DateTimeInput(),
    val searchState: SearchState = SearchState(),
) {
    constructor(drinkLog: DrinkLog) : this(
        selectedDrinkItems = drinkLog.items,
        dateTimeInput = DateTimeInput(date = drinkLog.date),
    )

    fun isValid(): Boolean =
        selectedDrinkItems.isNotEmpty() && !selectedDrinkItems.any { item -> item.name.isBlank() }

    fun toDrinkLog(id: Long = 0): DrinkLog = DrinkLog(
        id = id, date = dateTimeInput.toDate(), items = selectedDrinkItems
    )
}

data class SearchState(
    val input: TextInput = TextInput(),
    val selectedItem: DrinkItem? = null,
    val results: List<DrinkItem> = listOf(),
    val canCreateNewItem: Boolean = false,
) {
    fun toItem(): DrinkItem = DrinkItem(name = input.value.toDrinkItemName())
}

sealed interface DrinkEntryEvent {
    data object AddItem : DrinkEntryEvent
    data class RemoveItem(val drinkItem: DrinkItem) : DrinkEntryEvent
    data class UpdateSelectedSearchItem(val drinkItem: DrinkItem) : DrinkEntryEvent
    data class UpdateSearchInput(val input: String) : DrinkEntryEvent
    data object ClearSearch : DrinkEntryEvent
    data object CreateNewItemFromInput : DrinkEntryEvent
    data class UpdateDate(val date: LocalDate) : DrinkEntryEvent
    data class UpdateTime(val time: LocalTime) : DrinkEntryEvent
    data object Submit : DrinkEntryEvent
}
