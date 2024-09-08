package com.example.symptomtracker.feature.movement

import com.example.symptomtracker.core.domain.model.StoolType
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class AbstractMovementEntryViewModelTest {
    private lateinit var viewModel: TestMovementEntryViewModel

    @Before
    fun setup() {
        viewModel = TestMovementEntryViewModel()
    }

    @Test
    fun uiState_holdsDefaultValues_whenInitialised() =
        assertEquals(null, viewModel.uiState.chosenStoolType)

    @Test
    fun chosenStoolTypeUpdates_whenUpdateChosenStoolTypeIsCalled() = runTest {
        viewModel.updateChosenStoolType(StoolType.NORMAL_3)

        assertEquals(StoolType.NORMAL_3, viewModel.uiState.chosenStoolType)
    }

    @Test
    fun dateInputFieldsUpdate_whenUpdateDateIsCalled() = runTest {
        val date = LocalDate.of(2024, 2, 1)

        viewModel.updateDate(date)

        assertEquals(date, viewModel.uiState.dateTimeInput.date)
    }

    @Test
    fun timeInputFieldsUpdate_whenUpdateTimeIsCalled() = runTest {
        val time = LocalTime.of(10, 25)

        viewModel.updateTime(time)

        assertEquals(time, viewModel.uiState.dateTimeInput.time)
    }
}

class TestMovementEntryViewModel : AbstractMovementEntryViewModel() {
    override fun submit() {}
}
