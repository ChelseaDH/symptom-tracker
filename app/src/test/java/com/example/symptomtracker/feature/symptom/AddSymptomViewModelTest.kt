package com.example.symptomtracker.feature.symptom

import com.example.symptomtracker.core.domain.model.Symptom
import com.example.symptomtracker.core.domain.model.SymptomWithSeverity
import com.example.symptomtracker.core.testing.repository.TestSymptomRepository
import com.example.symptomtracker.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher

class AddSymptomViewModelTest {
    @get:Rule
    val dispatcherRule: TestWatcher = MainDispatcherRule()

    private val symptomRepository = TestSymptomRepository()
    private lateinit var viewModel: AddSymptomViewModel

    @Before
    fun setup() {
        viewModel = AddSymptomViewModel(symptomRepository = symptomRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun uiState_holdsDefaultValues_whenInitialised() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState }

        val symptoms = listOf(
            Symptom(id = 1, name = "bloating"),
            Symptom(id = 2, name = "fatigue"),
        )
        symptomRepository.sendSymptoms(symptoms)

        assertEquals(listOf<SymptomWithSeverity>(), viewModel.uiState.selectedSymptoms)
        assertEquals(
            SearchState(
                input = "",
                selectedSymptom = null,
                results = symptoms,
                canCreateNewSymptom = false,
            ), viewModel.uiState.searchState
        )
        assertNull(viewModel.uiState.selectedSeverity)

        collectJob.cancel()
    }
}
