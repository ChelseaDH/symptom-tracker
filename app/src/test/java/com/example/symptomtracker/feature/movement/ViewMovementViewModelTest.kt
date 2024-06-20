package com.example.symptomtracker.feature.movement

import androidx.lifecycle.SavedStateHandle
import com.example.symptomtracker.core.model.MovementLog
import com.example.symptomtracker.core.model.StoolType
import com.example.symptomtracker.core.testing.repository.TestMovementRepository
import com.example.symptomtracker.core.ui.ViewLogUiState
import com.example.symptomtracker.feature.movement.navigation.MOVEMENT_LOG_ID
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

class ViewMovementViewModelTest {
    @get:Rule
    val dispatcherRule: TestWatcher = MainDispatcherRule()

    private val movementRepository = TestMovementRepository()
    private lateinit var viewModel: ViewMovementViewModel

    @Before
    fun setup() {
        viewModel = ViewMovementViewModel(
            savedStateHandle = SavedStateHandle(mapOf(MOVEMENT_LOG_ID to 1)),
            movementRepository = movementRepository,
        )
    }

    @Test
    fun logId_matchesMovementLogIdFromSavedStateHandle() = assertEquals(1, viewModel.logId)

    @Test
    fun uiState_isLoading_whenFirstInitialised() = runTest {
        assertEquals(ViewLogUiState.Loading, viewModel.uiState.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun uiState_isEmpty_whenMovementLogDoesNotExist() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        movementRepository.sendMovementLogs(listOf())

        assertEquals(ViewLogUiState.Empty, viewModel.uiState.value)

        collectJob.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun uiState_holdsData_whenMovementLogExists() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        val movementLog =
            MovementLog(id = 1, date = OffsetDateTime.now(), stoolType = StoolType.NORMAL_3)

        movementRepository.sendMovementLogs(listOf(movementLog))

        assertEquals(ViewLogUiState.Data(movementLog), viewModel.uiState.value)

        collectJob.cancel()
    }
}
