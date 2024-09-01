package com.example.symptomtracker.feature.food

import androidx.lifecycle.SavedStateHandle
import com.example.symptomtracker.core.domain.model.FoodItem
import com.example.symptomtracker.core.testing.repository.TestFoodRepository
import com.example.symptomtracker.feature.food.navigation.PREFILL_ITEMS
import com.example.symptomtracker.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher

class AddFoodViewModelTest {
    @get:Rule
    val dispatcherRule: TestWatcher = MainDispatcherRule()

    private val foodLogRepository = TestFoodRepository()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun givenPrefillItemsAreNotSet_whenInitialised_stateHoldsDefaultValues() = runTest {
        val viewModel = AddFoodViewModel(
            foodLogRepository = foodLogRepository,
            savedStateHandle = SavedStateHandle()
        )
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState }

        val foodItems = listOf(
            FoodItem(id = 1, name = "oats"),
            FoodItem(id = 2, name = "blueberries"),
        )
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
    fun givenPrefillItemsAreSet_whenInitialised_stateHoldsSelectedFoodItemsWithOtherDefaultValues() =
        runTest {
            val viewModel = AddFoodViewModel(
                foodLogRepository = foodLogRepository,
                savedStateHandle = SavedStateHandle(mapOf(PREFILL_ITEMS to "[oats, apple]"))
            )
            val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState }

            val foodItems = listOf(
                FoodItem(id = 1, name = "Oats"),
                FoodItem(id = 2, name = "Blueberries"),
            )
            foodLogRepository.sendFoodItems(foodItems)

            assertEquals(
                listOf(FoodItem(name = "Oats"), FoodItem(name = "Apple")),
                viewModel.uiState.selectedFoodItems
            )
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
}
