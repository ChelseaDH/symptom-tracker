package com.example.symptomtracker.feature.movement

import androidx.lifecycle.SavedStateHandle
import com.example.symptomtracker.core.model.MovementLog
import com.example.symptomtracker.core.model.StoolType
import com.example.symptomtracker.core.testing.repository.TestMovementRepository
import com.example.symptomtracker.core.ui.DateInputFields
import com.example.symptomtracker.core.ui.DateTimeInput
import com.example.symptomtracker.core.ui.TimeInputFields
import com.example.symptomtracker.feature.movement.navigation.MOVEMENT_LOG_ID
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

class EditMovementViewModelTest {
    @get:Rule
    val dispatcherRule: TestWatcher = MainDispatcherRule()

    private val movementRepository = TestMovementRepository()
    private lateinit var viewModel: EditMovementViewModel

    @Before
    fun setup() {
        viewModel = EditMovementViewModel(
            savedStateHandle = SavedStateHandle(mapOf(MOVEMENT_LOG_ID to 1)),
            movementRepository = movementRepository,
        )
    }

    @Test
    fun logId_matchesMovementLogIdFromSavedStateHandle() = assertEquals(1, viewModel.logId)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun uiState_holdsDefaultValues_whenMovementLogDoesNotExist() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState }

        movementRepository.sendMovementLogs(listOf())

        assertEquals(null, viewModel.uiState.chosenStoolType)

        collectJob.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun uiState_holdsLogValues_whenMovementLogExists() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState }
        val movementLog =
            MovementLog(
                id = 1,
                date = OffsetDateTime.parse("2023-03-02T09:10:00+00:00"),
                stoolType = StoolType.NORMAL_3
            )

        movementRepository.sendMovementLogs(listOf(movementLog))

        assertEquals(
            MovementEntryUiState(
                chosenStoolType = StoolType.NORMAL_3,
                dateTimeInput = DateTimeInput(
                    dateInputFields = DateInputFields(year = 2023, month = 2, day = 2),
                    timeInputFields = TimeInputFields(hour = 9, minute = 10)
                )
            ),
            viewModel.uiState
        )

        collectJob.cancel()
    }
}