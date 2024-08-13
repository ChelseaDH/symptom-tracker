package com.example.symptomtracker.feature.food

import com.example.symptomtracker.core.model.FoodItem
import com.example.symptomtracker.core.testing.repository.TestFoodRepository
import com.example.symptomtracker.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher

class ManageItemsViewModelTest {
    @get:Rule
    val dispatcherRule: TestWatcher = MainDispatcherRule()

    private val foodLogRepository = TestFoodRepository()
    private lateinit var viewModel: ManageItemsViewModel

    @Before
    fun setup() {
        viewModel = ManageItemsViewModel(foodLogRepository = foodLogRepository)
    }

    @Test
    fun uiState_isLoading_whenFirstInitialised() = runTest {
        assertEquals(ManageItemsUiState.Loading, viewModel.uiState.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun uiState_holdsData_whenFoodItemsExist() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        val foodItems = listOf(
            FoodItem(id = 1, name = "oats"),
            FoodItem(id = 2, name = "blueberries"),
        )
        foodLogRepository.sendFoodItems(foodItems)

        assertEquals(ManageItemsUiState.Data(foodItems), viewModel.uiState.value)

        collectJob.cancel()
    }
}
