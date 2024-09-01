package com.example.symptomtracker.feature.movement

import com.example.symptomtracker.core.designsystem.component.DateInputFields
import com.example.symptomtracker.core.designsystem.component.TimeInputFields
import com.example.symptomtracker.core.domain.model.StoolType
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

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
        val dateInputFields = DateInputFields(year = 2024, month = 2, day = 1)

        viewModel.updateDate(dateInputFields)

        assertEquals(dateInputFields, viewModel.uiState.dateTimeInput.dateInputFields)
    }

    @Test
    fun timeInputFieldsUpdate_whenUpdateTimeIsCalled() = runTest {
        val timeInputField = TimeInputFields(hour = 10, minute = 25)

        viewModel.updateTime(timeInputField)

        assertEquals(timeInputField, viewModel.uiState.dateTimeInput.timeInputFields)
    }
}

class TestMovementEntryViewModel : AbstractMovementEntryViewModel() {
    override fun submit() {}
}
