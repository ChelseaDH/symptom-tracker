package com.example.symptomtracker.feature.food

import com.example.symptomtracker.core.designsystem.component.DateTimeInput
import com.example.symptomtracker.core.designsystem.component.TextInput
import com.example.symptomtracker.core.domain.model.FoodItem
import com.example.symptomtracker.core.domain.repository.FoodLogRepository
import com.example.symptomtracker.core.testing.repository.TestFoodRepository
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
                input = TextInput(value = "", validationError = null),
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
                input = TextInput(value = "", validationError = null),
                selectedItem = null,
                results = foodItems,
                canCreateNewItem = false,
            ), viewModel.uiState.searchState
        )

        viewModel.handleEvent(FoodEntryEvent.UpdateSearchInput(input = "hi"))

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

        foodLogRepository.sendFoodItems(foodItems)
        assertEquals(
            SearchState(
                input = TextInput(value = "", validationError = null),
                selectedItem = null,
                results = foodItems,
                canCreateNewItem = false,
            ), viewModel.uiState.searchState
        )

        viewModel.handleEvent(FoodEntryEvent.UpdateSearchInput(input = "oats"))

        assertEquals(
            SearchState(
                input = TextInput(value = "oats", validationError = null),
                selectedItem = null,
                results = listOf(FoodItem(id = 1, name = "oats")),
                canCreateNewItem = false,
            ), viewModel.uiState.searchState
        )

        viewModel.handleEvent(FoodEntryEvent.UpdateSearchInput(input = "oats "))

        assertEquals(
            SearchState(
                input = TextInput(value = "oats ", validationError = null),
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
                input = TextInput(value = "", validationError = null),
                selectedItem = null,
                results = foodItems,
                canCreateNewItem = false,
            ), viewModel.uiState.searchState
        )

        viewModel.handleEvent(FoodEntryEvent.UpdateSelectedSearchItem(foodItem = foodItems[0]))

        assertEquals(
            SearchState(
                input = TextInput(value = "oats", validationError = null),
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

        viewModel.handleEvent(FoodEntryEvent.UpdateSelectedSearchItem(foodItem = foodItems[0]))

        assertEquals(listOf<FoodItem>(), viewModel.uiState.selectedFoodItems)

        // Adding a food item
        viewModel.handleEvent(FoodEntryEvent.AddItem)

        // Asserting that the food item is added and the search state is reset
        assertEquals(
            listOf(foodItems[0]),
            viewModel.uiState.selectedFoodItems
        )
        assertEquals(
            SearchState(
                input = TextInput(value = "", validationError = null),
                selectedItem = null,
                results = foodItems,
                canCreateNewItem = false,
            ),
            viewModel.uiState.searchState
        )

        // Removing the food item
        viewModel.handleEvent(FoodEntryEvent.RemoveItem(foodItem = foodItems[0]))

        // Asserting that the selected symptoms is empty again
        assertEquals(listOf<FoodItem>(), viewModel.uiState.selectedFoodItems)

        collectJob.cancel()
    }

    @Test
    fun whenUpdateDateIsCalled_thenDateInputFieldsUpdate() = runTest {
        viewModel.uiState = FoodEntryUiState(
            dateTimeInput = DateTimeInput(date = LocalDate.now())
        )
        val date = LocalDate.of(2020, 5, 22)

        viewModel.handleEvent(FoodEntryEvent.UpdateDate(date))

        assertEquals(date, viewModel.uiState.dateTimeInput.date)
    }

    @Test
    fun timeInputFieldsUpdate_whenUpdateTimeIsCalled() = runTest {
        viewModel.uiState = FoodEntryUiState(
            dateTimeInput = DateTimeInput(time = LocalTime.now())
        )
        val time = LocalTime.of(12, 2, 22)

        viewModel.handleEvent(FoodEntryEvent.UpdateTime(time))

        assertEquals(time, viewModel.uiState.dateTimeInput.time)
    }
}

class TestFoodEntryViewModel(foodLogRepository: FoodLogRepository) :
    AbstractFoodEntryViewModel(foodLogRepository = foodLogRepository) {
    override fun submit() {}
}

val foodItems = listOf(
    FoodItem(id = 1, name = "oats"),
    FoodItem(id = 2, name = "blueberries"),
)
