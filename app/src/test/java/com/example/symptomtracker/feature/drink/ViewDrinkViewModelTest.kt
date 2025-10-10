package com.example.symptomtracker.feature.drink

import androidx.lifecycle.SavedStateHandle
import com.example.symptomtracker.core.domain.model.DrinkItem
import com.example.symptomtracker.core.domain.model.DrinkLog
import com.example.symptomtracker.core.testing.repository.TestDrinkRepository
import com.example.symptomtracker.core.ui.ViewLogUiState
import com.example.symptomtracker.feature.drink.navigation.DRINK_LOG_ID
import com.example.symptomtracker.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import java.time.OffsetDateTime

class ViewDrinkViewModelTest {
    @get:Rule
    val dispatcherRule: TestWatcher = MainDispatcherRule()

    private val drinkLogRepository = TestDrinkRepository()
    private lateinit var viewModel: ViewDrinkViewModel

    @Before
    fun setup() {
        viewModel = ViewDrinkViewModel(
            savedStateHandle = SavedStateHandle(mapOf(DRINK_LOG_ID to 1)),
            drinkLogRepository = drinkLogRepository
        )
    }

    @Test
    fun logId_matchesDrinkLogIdFromSavedStateHandle() = assertEquals(1, viewModel.logId)

    @Test
    fun uiState_isLoading_whenFirstInitialised() = runTest {
        assertEquals(ViewLogUiState.Loading, viewModel.uiState.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun uiState_isEmpty_whenDrinkLogDoesNotExist() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        drinkLogRepository.sendDrinkLogs(listOf())

        assertEquals(ViewLogUiState.Empty, viewModel.uiState.value)

        collectJob.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun uiState_holdsData_whenDrinkLogExists() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        val drinkLog = DrinkLog(
            id = 1,
            date = OffsetDateTime.parse("2023-03-02T09:10:00+00:00"),
            items = listOf(DrinkItem(id = 1, name = "water"))
        )
        drinkLogRepository.sendDrinkLogs(listOf(drinkLog))

        assertEquals(ViewLogUiState.Data(drinkLog), viewModel.uiState.value)

        collectJob.cancel()
    }

    @Test
    fun navigationEvent_isUpdatedCorrectly_whenEditEventIsHandled() = runTest {
        viewModel.handleEvent(ViewDrinkEvent.EditLog)

        assertEquals(NavigationEvent.NavigateToEdit, viewModel.navigationEvent.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun navigationEvent_isUpdatedCorrectly_whenCopyEventIsHandled() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        val drinkLog = DrinkLog(
            id = 1,
            date = OffsetDateTime.parse("2023-03-02T09:10:00+00:00"),
            items = listOf(DrinkItem(id = 1, name = "water"))
        )
        drinkLogRepository.sendDrinkLogs(listOf(drinkLog))

        viewModel.handleEvent(ViewDrinkEvent.CopyLog)

        assertEquals(NavigationEvent.NavigateToCopy(drinkLog), viewModel.navigationEvent.value)

        collectJob.cancel()
    }

    @Test
    fun navigationEvent_isUpdatedCorrectly_whenNavigationHandledEventIsHandled() = runTest {
        viewModel.handleEvent(ViewDrinkEvent.EditLog)
        assertEquals(NavigationEvent.NavigateToEdit, viewModel.navigationEvent.value)

        viewModel.handleEvent(ViewDrinkEvent.NavigationHandled)
        assertNull(viewModel.navigationEvent.value)
    }
}
