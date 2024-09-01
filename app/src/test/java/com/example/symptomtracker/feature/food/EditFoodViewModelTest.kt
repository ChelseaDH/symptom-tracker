package com.example.symptomtracker.feature.food

import androidx.lifecycle.SavedStateHandle
import com.example.symptomtracker.core.designsystem.component.DateInputFields
import com.example.symptomtracker.core.designsystem.component.DateTimeInput
import com.example.symptomtracker.core.designsystem.component.TimeInputFields
import com.example.symptomtracker.core.domain.model.FoodItem
import com.example.symptomtracker.core.domain.model.FoodLog
import com.example.symptomtracker.core.testing.repository.TestFoodRepository
import com.example.symptomtracker.feature.food.navigation.FOOD_LOG_ID
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
import java.time.OffsetDateTime

class EditFoodViewModelTest {
    @get:Rule
    val dispatcherRule: TestWatcher = MainDispatcherRule()

    private val foodLogRepository = TestFoodRepository()
    private lateinit var viewModel: EditFoodViewModel

    @Before
    fun setup() {
        viewModel = EditFoodViewModel(
            savedStateHandle = SavedStateHandle(mapOf(FOOD_LOG_ID to 1)),
            foodLogRepository = foodLogRepository
        )
    }

    @Test
    fun logId_matchesFoodLogIdFromSavedStateHandle() = assertEquals(1, viewModel.logId)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun uiState_holdsDefaultValues_whenFoodLogDoesNotExist() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState }

        foodLogRepository.sendFoodLogs(listOf())

        assertEquals(listOf<FoodItem>(), viewModel.uiState.selectedFoodItems)

        collectJob.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun uiState_holdsLogValues_whenFoodLogExists() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState }

        val foodItem = FoodItem(id = 1, name = "oats")
        val foodItems = listOf(
            foodItem,
            FoodItem(id = 2, name = "blueberries"),
        )
        foodLogRepository.sendFoodItems(foodItems)

        val foodLog = FoodLog(
            id = 1,
            date = OffsetDateTime.parse("2023-03-02T09:10:00+00:00"),
            items = listOf(foodItem)
        )
        foodLogRepository.sendFoodLogs(listOf(foodLog))

        assertEquals(
            FoodEntryUiState(
                selectedFoodItems = listOf(foodItem),
                dateTimeInput = DateTimeInput(
                    dateInputFields = DateInputFields(year = 2023, month = 2, day = 2),
                    timeInputFields = TimeInputFields(hour = 9, minute = 10)
                ),
                searchState = SearchState(
                    input = "",
                    selectedItem = null,
                    results = foodItems,
                    canCreateNewItem = false,
                )
            ),
            viewModel.uiState
        )

        collectJob.cancel()
    }
}
