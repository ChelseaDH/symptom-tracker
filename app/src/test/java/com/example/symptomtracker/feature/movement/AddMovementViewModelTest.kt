package com.example.symptomtracker.feature.movement

import androidx.lifecycle.SavedStateHandle
import com.example.symptomtracker.core.testing.repository.TestMovementRepository
import com.example.symptomtracker.navigation.DATE_ARG
import com.example.symptomtracker.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import java.time.LocalDate

class AddMovementViewModelTest {
    @get:Rule
    val dispatcherRule: TestWatcher = MainDispatcherRule()
    private val movementRepository = TestMovementRepository()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun initialisation_whenNoArgsAreSet_uiStateContainsDefaultValues() = runTest {
        val viewModel = AddMovementViewModel(
            movementRepository = movementRepository,
            savedStateHandle = SavedStateHandle(),
        )
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState }

        assertEquals(LocalDate.now(), viewModel.uiState.dateTimeInput.date)
        assertNull(viewModel.uiState.chosenStoolType)

        collectJob.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun initialisation_whenArgsAreSet_uiStateContainsArgValues() = runTest {
        val viewModel = AddMovementViewModel(
            movementRepository = movementRepository,
            savedStateHandle = SavedStateHandle(mapOf(DATE_ARG to "2025-05-01")),
        )
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState }

        assertEquals(LocalDate.parse("2025-05-01"), viewModel.uiState.dateTimeInput.date)
        assertNull(viewModel.uiState.chosenStoolType)

        collectJob.cancel()
    }
}
