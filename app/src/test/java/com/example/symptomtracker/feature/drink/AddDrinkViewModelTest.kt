package com.example.symptomtracker.feature.drink

import androidx.lifecycle.SavedStateHandle
import com.example.symptomtracker.core.designsystem.component.TextInput
import com.example.symptomtracker.core.domain.model.DrinkItem
import com.example.symptomtracker.core.testing.repository.TestDrinkRepository
import com.example.symptomtracker.feature.drink.navigation.PREFILL_ITEMS
import com.example.symptomtracker.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher

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
    fun givenPrefillItemsAreSet_whenInitialised_stateHoldsSelectedDrinkItemsWithOtherDefaultValues() =
        runTest {
            val viewModel = AddDrinkViewModel(
                drinkLogRepository = drinkLogRepository,
                savedStateHandle = SavedStateHandle(mapOf(PREFILL_ITEMS to "[water, tea]"))
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
