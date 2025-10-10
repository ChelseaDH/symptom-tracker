package com.example.symptomtracker.feature.drink

import androidx.lifecycle.SavedStateHandle
import com.example.symptomtracker.core.designsystem.component.DateTimeInput
import com.example.symptomtracker.core.designsystem.component.TextInput
import com.example.symptomtracker.core.domain.model.DrinkItem
import com.example.symptomtracker.core.domain.model.DrinkLog
import com.example.symptomtracker.core.testing.repository.TestDrinkRepository
import com.example.symptomtracker.feature.drink.navigation.DRINK_LOG_ID
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
import java.time.OffsetDateTime

class EditDrinkViewModelTest {
    @get:Rule
    val dispatcherRule: TestWatcher = MainDispatcherRule()

    private val drinkLogRepository = TestDrinkRepository()
    private lateinit var viewModel: EditDrinkViewModel

    @Before
    fun setup() {
        viewModel = EditDrinkViewModel(
            savedStateHandle = SavedStateHandle(mapOf(DRINK_LOG_ID to 1)),
            drinkLogRepository = drinkLogRepository
        )
    }

    @Test
    fun logId_matchesDrinkLogIdFromSavedStateHandle() = assertEquals(1, viewModel.logId)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun uiState_holdsDefaultValues_whenDrinkLogDoesNotExist() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState }

        drinkLogRepository.sendDrinkLogs(listOf())

        assertEquals(listOf<DrinkItem>(), viewModel.uiState.selectedDrinkItems)

        collectJob.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun uiState_holdsLogValues_whenDrinkLogExists() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState }

        val drinkItem = DrinkItem(id = 1, name = "water")
        val drinkItems = listOf(
            drinkItem,
            DrinkItem(id = 2, name = "coffee"),
        )
        drinkLogRepository.sendDrinkItems(drinkItems)

        val drinkLog = DrinkLog(
            id = 1,
            date = OffsetDateTime.parse("2023-03-02T09:10:00+00:00"),
            items = listOf(drinkItem)
        )
        drinkLogRepository.sendDrinkLogs(listOf(drinkLog))

        assertEquals(
            DrinkEntryUiState(
                selectedDrinkItems = listOf(drinkItem),
                dateTimeInput = DateTimeInput(
                    date = LocalDate.of(2023, 3, 2),
                    time = LocalTime.of(9, 10)
                ),
                searchState = SearchState(
                    input = TextInput(value = "", validationError = null),
                    selectedItem = null,
                    results = drinkItems,
                    canCreateNewItem = false,
                )
            ),
            viewModel.uiState
        )

        collectJob.cancel()
    }
}
