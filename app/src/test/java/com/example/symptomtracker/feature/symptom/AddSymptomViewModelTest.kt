package com.example.symptomtracker.feature.symptom

import androidx.lifecycle.SavedStateHandle
import com.example.symptomtracker.core.designsystem.component.TextInput
import com.example.symptomtracker.core.domain.model.Symptom
import com.example.symptomtracker.core.domain.model.SymptomWithSeverity
import com.example.symptomtracker.core.testing.repository.TestSymptomRepository
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

class AddSymptomViewModelTest {
    @get:Rule
    val dispatcherRule: TestWatcher = MainDispatcherRule()

    private val symptomRepository = TestSymptomRepository()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun initialisation_whenNoArgsAreSet_uiStateContainsDefaultValues() = runTest {
        val viewModel = AddSymptomViewModel(
            symptomRepository = symptomRepository,
            savedStateHandle = SavedStateHandle(),
        )
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState }

        val symptoms = listOf(
            Symptom(id = 1, name = "bloating"),
            Symptom(id = 2, name = "fatigue"),
        )
        symptomRepository.sendSymptoms(symptoms)

        assertEquals(listOf<SymptomWithSeverity>(), viewModel.uiState.selectedSymptoms)
        assertEquals(LocalDate.now(), viewModel.uiState.dateTimeInput.date)
        assertEquals(
            SearchState(
                input = TextInput(value = "", validationError = null),
                selectedSymptom = null,
                results = symptoms,
                canCreateNewSymptom = false,
            ), viewModel.uiState.searchState
        )
        assertNull(viewModel.uiState.selectedSeverity)

        collectJob.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun initialisation_whenArgsAreSet_uiStateContainsArgValues() = runTest {
        val viewModel = AddSymptomViewModel(
            symptomRepository = symptomRepository,
            savedStateHandle = SavedStateHandle(mapOf(DATE_ARG to "2025-05-01")),
        )
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState }

        val symptoms = listOf(
            Symptom(id = 1, name = "bloating"),
            Symptom(id = 2, name = "fatigue"),
        )
        symptomRepository.sendSymptoms(symptoms)

        assertEquals(listOf<SymptomWithSeverity>(), viewModel.uiState.selectedSymptoms)
        assertEquals(LocalDate.parse("2025-05-01"), viewModel.uiState.dateTimeInput.date)
        assertEquals(
            SearchState(
                input = TextInput(value = "", validationError = null),
                selectedSymptom = null,
                results = symptoms,
                canCreateNewSymptom = false,
            ), viewModel.uiState.searchState
        )
        assertNull(viewModel.uiState.selectedSeverity)

        collectJob.cancel()
    }
}
