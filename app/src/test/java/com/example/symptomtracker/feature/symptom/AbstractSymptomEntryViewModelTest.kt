package com.example.symptomtracker.feature.symptom

import com.example.symptomtracker.core.data.repository.SymptomRepository
import com.example.symptomtracker.core.model.Severity
import com.example.symptomtracker.core.model.Symptom
import com.example.symptomtracker.core.model.SymptomWithSeverity
import com.example.symptomtracker.core.testing.repository.TestSymptomRepository
import com.example.symptomtracker.core.ui.DateInputFields
import com.example.symptomtracker.core.ui.TimeInputFields
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

class AbstractSymptomEntryViewModelTest {
    @get:Rule
    val dispatcherRule: TestWatcher = MainDispatcherRule()

    private val symptomRepository = TestSymptomRepository()
    private lateinit var viewModel: TestSymptomEntryViewModel

    @Before
    fun setup() {
        viewModel = TestSymptomEntryViewModel(symptomRepository)
    }

    @Test
    fun uiState_holdsDefaultValues_whenInitialised() {
        assertEquals(listOf<SymptomWithSeverity>(), viewModel.uiState.selectedSymptoms)
        assertEquals(
            SearchState(
                input = "",
                selectedSymptom = null,
                results = listOf(),
                canCreateNewSymptom = false,
            ),
            viewModel.uiState.searchState
        )
        assertNull(viewModel.uiState.selectedSeverity)
    }

    @Test
    fun selectedSeverityUpdates_whenUpdateSelectedSeverityIsCalled() = run {
        assertNull(viewModel.uiState.selectedSeverity)

        viewModel.updateSelectedSeverity(Severity.MILD)

        assertEquals(Severity.MILD, viewModel.uiState.selectedSeverity)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun searchInputUpdates_whenUpdateSearchInputIsCalled_andInputtedSymptomDoesNotExist() =
        runTest {
            val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState }

            val symptom = Symptom(id = 1, name = "bloating")
            symptomRepository.sendSymptoms(listOf(symptom))

            assertEquals(
                SearchState(
                    input = "",
                    selectedSymptom = null,
                    results = listOf(symptom),
                    canCreateNewSymptom = false,
                ),
                viewModel.uiState.searchState
            )

            viewModel.updateSearchInput("hi")

            assertEquals(
                SearchState(
                    input = "hi",
                    selectedSymptom = null,
                    results = listOf(),
                    canCreateNewSymptom = true,
                ),
                viewModel.uiState.searchState
            )

            collectJob.cancel()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun searchInputUpdates_whenUpdateSearchInputIsCalled_andInputtedSymptomExists() =
        runTest {
            val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState }

            val symptom = Symptom(id = 1, name = "bloating")
            symptomRepository.sendSymptoms(listOf(symptom))

            assertEquals(
                SearchState(
                    input = "",
                    selectedSymptom = null,
                    results = listOf(symptom),
                    canCreateNewSymptom = false,
                ),
                viewModel.uiState.searchState
            )

            viewModel.updateSearchInput("bloating")

            assertEquals(
                SearchState(
                    input = "bloating",
                    selectedSymptom = null,
                    results = listOf(symptom),
                    canCreateNewSymptom = false,
                ),
                viewModel.uiState.searchState
            )

            collectJob.cancel()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun searchInputUpdates_whenUpdateSelectedSearchSymptomIsCalled() =
        runTest {
            val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState }

            val symptom = Symptom(id = 1, name = "bloating")
            symptomRepository.sendSymptoms(listOf(symptom))

            assertEquals(
                SearchState(
                    input = "",
                    selectedSymptom = null,
                    results = listOf(symptom),
                    canCreateNewSymptom = false,
                ),
                viewModel.uiState.searchState
            )

            viewModel.updateSelectedSearchSymptom(symptom)

            assertEquals(
                SearchState(
                    input = "bloating",
                    selectedSymptom = symptom,
                    results = listOf(symptom),
                    canCreateNewSymptom = false,
                ),
                viewModel.uiState.searchState
            )

            collectJob.cancel()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun uiStateUpdates_whenSymptomsAreAddedAndRemoved() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState }

        // Setting up uiState to be valid for adding a symptom
        val symptom = Symptom(id = 1, name = "bloating")
        symptomRepository.sendSymptoms(listOf(symptom))

        viewModel.updateSelectedSeverity(Severity.MILD)
        viewModel.updateSelectedSearchSymptom(symptom)

        assertEquals(listOf<SymptomWithSeverity>(), viewModel.uiState.selectedSymptoms)

        // Adding a symptom
        viewModel.addSymptomWithSeverity()

        // Asserting that the symptom is added and the search state and selected severity are reset
        assertEquals(
            listOf(SymptomWithSeverity(symptom = symptom, severity = Severity.MILD)),
            viewModel.uiState.selectedSymptoms
        )
        assertEquals(
            SearchState(
                input = "",
                selectedSymptom = null,
                results = listOf(symptom),
                canCreateNewSymptom = false,
            ),
            viewModel.uiState.searchState
        )
        assertNull(viewModel.uiState.selectedSeverity)

        // Removing the symptom
        viewModel.removeSymptom(SymptomWithSeverity(symptom = symptom, severity = Severity.MILD))

        // Asserting that the selected symptoms is empty again
        assertEquals(listOf<SymptomWithSeverity>(), viewModel.uiState.selectedSymptoms)

        collectJob.cancel()
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

class TestSymptomEntryViewModel(symptomRepository: SymptomRepository) :
    AbstractSymptomEntryViewModel(symptomRepository = symptomRepository) {
    override suspend fun submit() {}
}
