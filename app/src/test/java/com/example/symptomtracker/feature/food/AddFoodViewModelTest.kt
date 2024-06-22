package com.example.symptomtracker.feature.food

import com.example.symptomtracker.core.model.FoodItem
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

class AddFoodViewModelTest {
    @get:Rule
    val dispatcherRule: TestWatcher = MainDispatcherRule()

    private val foodLogRepository = TestFoodRepository()
    private lateinit var viewModel: AddFoodViewModel

    @Before
    fun setup() {
        viewModel = AddFoodViewModel(foodLogRepository = foodLogRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun uiState_holdsDefaultValues_whenInitialised() = runTest {
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
}
