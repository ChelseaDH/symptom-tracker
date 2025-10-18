package com.example.symptomtracker.feature.drink

import androidx.lifecycle.SavedStateHandle
import com.example.symptomtracker.core.designsystem.component.TextInput
import com.example.symptomtracker.core.domain.model.DrinkItem
import com.example.symptomtracker.core.testing.repository.TestDrinkRepository
import com.example.symptomtracker.navigation.DATE_ARG
import com.example.symptomtracker.navigation.PREFILL_ITEMS
import com.example.symptomtracker.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import java.time.LocalDate

class AddDrinkViewModelTest {
    @get:Rule
    val dispatcherRule: TestWatcher = MainDispatcherRule()

    private val drinkLogRepository = TestDrinkRepository()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun givenPrefillItemsAreNotSet_whenInitialised_stateHoldsDefaultValues() = runTest {
        val viewModel = AddDrinkViewModel(
            drinkLogRepository = drinkLogRepository, savedStateHandle = SavedStateHandle()
        )
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState }

        val drinkItems = listOf(
            DrinkItem(id = 1, name = "water"),
            DrinkItem(id = 2, name = "coffee"),
        )
        drinkLogRepository.sendDrinkItems(drinkItems)

        assertEquals(listOf<DrinkItem>(), viewModel.uiState.selectedDrinkItems)
        assertEquals(LocalDate.now(), viewModel.uiState.dateTimeInput.date)
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
    fun initialisation_whenPrefillItemsAndDateAreSet_setsItemsAndDateInUiState() =
        runTest {
            val viewModel = AddDrinkViewModel(
                drinkLogRepository = drinkLogRepository,
                savedStateHandle = SavedStateHandle(
                    mapOf(
                        PREFILL_ITEMS to "[water, tea]",
                        DATE_ARG to "2025-05-01",
                    )
                )
            )
            val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState }

            val drinkItems = listOf(
                DrinkItem(id = 1, name = "Water"),
                DrinkItem(id = 2, name = "Coffee"),
            )
            drinkLogRepository.sendDrinkItems(drinkItems)

            assertEquals(
                listOf(DrinkItem(name = "Water"), DrinkItem(name = "Tea")),
                viewModel.uiState.selectedDrinkItems
            )
            assertEquals(LocalDate.parse("2025-05-01"), viewModel.uiState.dateTimeInput.date)
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
}
