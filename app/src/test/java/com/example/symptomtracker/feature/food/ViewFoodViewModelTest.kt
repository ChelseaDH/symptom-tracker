package com.example.symptomtracker.feature.food

import androidx.lifecycle.SavedStateHandle
import com.example.symptomtracker.core.model.FoodItem
import com.example.symptomtracker.core.model.FoodLog
import com.example.symptomtracker.core.testing.repository.TestFoodRepository
import com.example.symptomtracker.core.ui.ViewLogUiState
import com.example.symptomtracker.feature.food.navigation.FOOD_LOG_ID
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
import java.time.OffsetDateTime

class ViewFoodViewModelTest {
    @get:Rule
    val dispatcherRule: TestWatcher = MainDispatcherRule()

    private val foodLogRepository = TestFoodRepository()
    private lateinit var viewModel: ViewFoodViewModel

    @Before
    fun setup() {
        viewModel = ViewFoodViewModel(
            savedStateHandle = SavedStateHandle(mapOf(FOOD_LOG_ID to 1)),
            foodLogRepository = foodLogRepository
        )
    }

    @Test
    fun logId_matchesFoodLogIdFromSavedStateHandle() = assertEquals(1, viewModel.logId)

    @Test
    fun uiState_isLoading_whenFirstInitialised() = runTest {
        assertEquals(ViewLogUiState.Loading, viewModel.uiState.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun uiState_isEmpty_whenFoodLogDoesNotExist() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        foodLogRepository.sendFoodLogs(listOf())

        assertEquals(ViewLogUiState.Empty, viewModel.uiState.value)

        collectJob.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun uiState_holdsData_whenFoodLogExists() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        val foodLog = FoodLog(
            id = 1,
            date = OffsetDateTime.parse("2023-03-02T09:10:00+00:00"),
            items = listOf(FoodItem(id = 1, name = "oats"))
        )
        foodLogRepository.sendFoodLogs(listOf(foodLog))

        assertEquals(ViewLogUiState.Data(foodLog), viewModel.uiState.value)

        collectJob.cancel()
    }
}
