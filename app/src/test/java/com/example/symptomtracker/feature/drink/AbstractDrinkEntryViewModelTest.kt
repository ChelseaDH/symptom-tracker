package com.example.symptomtracker.feature.drink

import com.example.symptomtracker.core.designsystem.component.DateTimeInput
import com.example.symptomtracker.core.designsystem.component.TextInput
import com.example.symptomtracker.core.domain.model.DrinkItem
import com.example.symptomtracker.core.domain.repository.DrinkLogRepository
import com.example.symptomtracker.core.testing.repository.TestDrinkRepository
import com.example.symptomtracker.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import java.time.LocalDate
import java.time.LocalTime

class AbstractDrinkEntryViewModelTest {
    @get:Rule
    val dispatcherRule: TestWatcher = MainDispatcherRule()

    private val drinkLogRepository = TestDrinkRepository()
    private lateinit var viewModel: AbstractDrinkEntryViewModel

    @Before
    fun setup() {
        viewModel = TestDrinkEntryViewModel(drinkLogRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun uiState_holdsDefaultValues_whenInitialised() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState }

        drinkLogRepository.sendDrinkItems(drinkItems)

        assertEquals(listOf<DrinkItem>(), viewModel.uiState.selectedDrinkItems)
        assertEquals(
            SearchState(
                input = TextInput(value = "", validationError = null),
                selectedItem = null,
                results = drinkItems,
                canCreateNewItem = false,
            ), viewModel.uiState.searchState
        )

        collectJob.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun searchInputUpdates_whenUpdateSearchInputIsCalled_andInputtedItemDoesNotExist() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState }

        drinkLogRepository.sendDrinkItems(drinkItems)
        assertEquals(
            SearchState(
                input = TextInput(value = "", validationError = null),
                selectedItem = null,
                results = drinkItems,
                canCreateNewItem = false,
            ), viewModel.uiState.searchState
        )

        viewModel.handleEvent(DrinkEntryEvent.UpdateSearchInput(input = "hi"))

        assertEquals(
            SearchState(
                input = TextInput(value = "hi", validationError = null),
                selectedItem = null,
                results = listOf(),
                canCreateNewItem = true,
            ), viewModel.uiState.searchState
        )

        collectJob.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun searchInputUpdates_whenUpdateSearchInputIsCalled_andInputtedItemExists() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState }

        drinkLogRepository.sendDrinkItems(drinkItems)
        assertEquals(
            SearchState(
                input = TextInput(value = "", validationError = null),
                selectedItem = null,
                results = drinkItems,
                canCreateNewItem = false,
            ), viewModel.uiState.searchState
        )

        viewModel.handleEvent(DrinkEntryEvent.UpdateSearchInput(input = "water"))

        assertEquals(
            SearchState(
                input = TextInput(value = "water", validationError = null),
                selectedItem = null,
                results = listOf(DrinkItem(id = 1, name = "water")),
                canCreateNewItem = false,
            ), viewModel.uiState.searchState
        )

        viewModel.handleEvent(DrinkEntryEvent.UpdateSearchInput(input = "water "))

        assertEquals(
            SearchState(
                input = TextInput(value = "water ", validationError = null),
                selectedItem = null,
                results = listOf(DrinkItem(id = 1, name = "water")),
                canCreateNewItem = false,
            ), viewModel.uiState.searchState
        )

        collectJob.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun searchInputUpdates_whenUpdateSelectedSearchItemIsCalled() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState }

        drinkLogRepository.sendDrinkItems(drinkItems)
        assertEquals(
            SearchState(
                input = TextInput(value = "", validationError = null),
                selectedItem = null,
                results = drinkItems,
                canCreateNewItem = false,
            ), viewModel.uiState.searchState
        )

        viewModel.handleEvent(DrinkEntryEvent.UpdateSelectedSearchItem(drinkItem = drinkItems[0]))

        assertEquals(
            SearchState(
                input = TextInput(value = "water", validationError = null),
                selectedItem = drinkItems[0],
                results = listOf(drinkItems[0]),
                canCreateNewItem = false,
            ), viewModel.uiState.searchState
        )

        collectJob.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun uiStateUpdates_whenDrinkItemsAreAddedAndRemoved() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState }

        // Setting up uiState to be valid for adding a drink item
        drinkLogRepository.sendDrinkItems(drinkItems)

        viewModel.handleEvent(DrinkEntryEvent.UpdateSelectedSearchItem(drinkItem = drinkItems[0]))

        assertEquals(listOf<DrinkItem>(), viewModel.uiState.selectedDrinkItems)

        // Adding a drink item
        viewModel.handleEvent(DrinkEntryEvent.AddItem)

        // Asserting that the drink item is added and the search state is reset
        assertEquals(
            listOf(drinkItems[0]), viewModel.uiState.selectedDrinkItems
        )
        assertEquals(
            SearchState(
                input = TextInput(value = "", validationError = null),
                selectedItem = null,
                results = drinkItems,
                canCreateNewItem = false,
            ), viewModel.uiState.searchState
        )

        // Removing the drink item
        viewModel.handleEvent(DrinkEntryEvent.RemoveItem(drinkItem = drinkItems[0]))

        // Asserting that the selected items is empty again
        assertEquals(listOf<DrinkItem>(), viewModel.uiState.selectedDrinkItems)

        collectJob.cancel()
    }

    @Test
    fun whenUpdateDateIsCalled_thenDateInputFieldsUpdate() = runTest {
        viewModel.uiState = DrinkEntryUiState(
            dateTimeInput = DateTimeInput(date = LocalDate.now())
        )
        val date = LocalDate.of(2020, 5, 22)

        viewModel.handleEvent(DrinkEntryEvent.UpdateDate(date))

        assertEquals(date, viewModel.uiState.dateTimeInput.date)
    }

    @Test
    fun timeInputFieldsUpdate_whenUpdateTimeIsCalled() = runTest {
        viewModel.uiState = DrinkEntryUiState(
            dateTimeInput = DateTimeInput(time = LocalTime.now())
        )
        val time = LocalTime.of(12, 2, 22)

        viewModel.handleEvent(DrinkEntryEvent.UpdateTime(time))

        assertEquals(time, viewModel.uiState.dateTimeInput.time)
    }
}

class TestDrinkEntryViewModel(drinkLogRepository: DrinkLogRepository) :
    AbstractDrinkEntryViewModel(drinkLogRepository = drinkLogRepository) {
    override fun submit() {}
}

val drinkItems = listOf(
    DrinkItem(id = 1, name = "water"),
    DrinkItem(id = 2, name = "coffee"),
)
