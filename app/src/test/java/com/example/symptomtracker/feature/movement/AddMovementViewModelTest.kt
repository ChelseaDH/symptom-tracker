package com.example.symptomtracker.feature.movement

import com.example.symptomtracker.core.testing.repository.TestMovementRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AddMovementViewModelTest {
    private val movementRepository = TestMovementRepository()
    private lateinit var viewModel: AddMovementViewModel

    @Before
    fun setup() {
        viewModel = AddMovementViewModel(movementRepository = movementRepository)
    }

    @Test
    fun uiState_holdsDefaultValues_whenInitialised() =
        assertEquals(null, viewModel.uiState.chosenStoolType)
}
