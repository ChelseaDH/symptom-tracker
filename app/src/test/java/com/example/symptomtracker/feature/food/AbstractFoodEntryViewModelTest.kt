package com.example.symptomtracker.feature.food

import com.example.symptomtracker.core.data.repository.FoodLogRepository
import com.example.symptomtracker.core.model.FoodItem
import com.example.symptomtracker.core.testing.repository.TestFoodRepository
import com.example.symptomtracker.core.ui.DateInputFields
import com.example.symptomtracker.core.ui.TimeInputFields
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

class AbstractFoodEntryViewModelTest {
    @get:Rule
    val dispatcherRule: TestWatcher = MainDispatcherRule()

    private val foodLogRepository = TestFoodRepository()
    private lateinit var viewModel: AbstractFoodEntryViewModel

    @Before
    fun setup() {
        viewModel = TestFoodEntryViewModel(foodLogRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun uiState_holdsDefaultValues_whenInitialised() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState }

        foodLogRepository.sendFoodItems(foodItems)

        assertEquals(listOf<FoodItem>(), viewModel.uiState.selectedFoodItems)
        assertEquals(
            SearchState(
                input = "",
                selectedItem = null,
                results = foodItems,
                canCreateNewItem = false,
            ), viewModel.uiState.searchState
        )

        collectJob.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun searchInputUpdates_whenUpdateSearchInputIsCalled_andInputtedItemDoesNotExist() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState }

        foodLogRepository.sendFoodItems(foodItems)
        assertEquals(
            SearchState(
                input = "",
                selectedItem = null,
                results = foodItems,
                canCreateNewItem = false,
            ), viewModel.uiState.searchState
        )

        viewModel.updateSearchInput("hi")

        assertEquals(
            SearchState(
                input = "hi",
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

        foodLogRepository.sendFoodItems(foodItems)
        assertEquals(
            SearchState(
                input = "",
                selectedItem = null,
                results = foodItems,
                canCreateNewItem = false,
            ), viewModel.uiState.searchState
        )

        viewModel.updateSearchInput("oats")

        assertEquals(
            SearchState(
                input = "oats",
                selectedItem = null,
                results = listOf(FoodItem(id = 1, name = "oats")),
                canCreateNewItem = false,
            ), viewModel.uiState.searchState
        )

        collectJob.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun searchInputUpdates_whenUpdateSelectedSearchItemIsCalled() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState }

        foodLogRepository.sendFoodItems(foodItems)
        assertEquals(
            SearchState(
                input = "",
                selectedItem = null,
                results = foodItems,
                canCreateNewItem = false,
            ), viewModel.uiState.searchState
        )

        viewModel.updateSelectedSearchItem(foodItems[0])

        assertEquals(
            SearchState(
                input = "oats",
                selectedItem = foodItems[0],
                results = listOf(foodItems[0]),
                canCreateNewItem = false,
            ), viewModel.uiState.searchState
        )

        collectJob.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun uiStateUpdates_whenFoodItemsAreAddedAndRemoved() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState }

        // Setting up uiState to be valid for adding a food item
        foodLogRepository.sendFoodItems(foodItems)

        viewModel.updateSelectedSearchItem(foodItems[0])

        assertEquals(listOf<FoodItem>(), viewModel.uiState.selectedFoodItems)

        // Adding a food item
        viewModel.addItem()

        // Asserting that the food item is added and the search state is reset
        assertEquals(
            listOf(foodItems[0]),
            viewModel.uiState.selectedFoodItems
        )
        assertEquals(
            SearchState(
                input = "",
                selectedItem = null,
                results = foodItems,
                canCreateNewItem = false,
            ),
            viewModel.uiState.searchState
        )

        // Removing the food item
        viewModel.removeItem(foodItems[0])

        // Asserting that the selected symptoms is empty again
        assertEquals(listOf<FoodItem>(), viewModel.uiState.selectedFoodItems)

        collectJob.cancel()
    }

    @Test
    fun dateInputFieldsUpdate_whenUpdateDateIsCalled() = runTest {
        val dateInputFields = DateInputFields(year = 2024, month = 2, day = 1)

        viewModel.updateDate(dateInputFields)

        assertEquals(dateInputFields, viewModel.uiState.dateTimeInput.dateInputFields)
    }

    @Test
    fun timeInputFieldsUpdate_whenUpdateTimeIsCalled() = runTest {
        val timeInputField = TimeInputFields(hour = 10, minute = 25)

        viewModel.updateTime(timeInputField)

        assertEquals(timeInputField, viewModel.uiState.dateTimeInput.timeInputFields)
    }
}

class TestFoodEntryViewModel(foodLogRepository: FoodLogRepository) :
    AbstractFoodEntryViewModel(foodLogRepository = foodLogRepository) {
    override suspend fun submit() {}
}

val foodItems = listOf(
    FoodItem(id = 1, name = "oats"),
    FoodItem(id = 2, name = "blueberries"),
)
