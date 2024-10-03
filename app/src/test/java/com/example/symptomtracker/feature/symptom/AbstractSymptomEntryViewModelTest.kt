package com.example.symptomtracker.feature.symptom

import com.example.symptomtracker.core.designsystem.component.TextInput
import com.example.symptomtracker.core.domain.model.Severity
import com.example.symptomtracker.core.domain.model.Symptom
import com.example.symptomtracker.core.domain.model.SymptomWithSeverity
import com.example.symptomtracker.core.domain.repository.SymptomRepository
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
import java.time.LocalDate
import java.time.LocalTime

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
                input = TextInput(value = "", validationError = null),
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

        viewModel.handleEvent(SymptomEntryEvent.UpdateSelectedSeverity(Severity.MILD))

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
                    input = TextInput(value = "", validationError = null),
                    selectedSymptom = null,
                    results = listOf(symptom),
                    canCreateNewSymptom = false,
                ),
                viewModel.uiState.searchState
            )

            viewModel.handleEvent(SymptomEntryEvent.UpdateSearchInput("hi"))

            assertEquals(
                SearchState(
                    input = TextInput(value = "hi", validationError = null),
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
                    input = TextInput(value = "", validationError = null),
                    selectedSymptom = null,
                    results = listOf(symptom),
                    canCreateNewSymptom = false,
                ),
                viewModel.uiState.searchState
            )

            viewModel.handleEvent(SymptomEntryEvent.UpdateSearchInput("bloating"))

            assertEquals(
                SearchState(
                    input = TextInput(value = "bloating", validationError = null),
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
                    input = TextInput(value = "", validationError = null),
                    selectedSymptom = null,
                    results = listOf(symptom),
                    canCreateNewSymptom = false,
                ),
                viewModel.uiState.searchState
            )

            viewModel.handleEvent(SymptomEntryEvent.UpdateSelectedSearchSymptom(symptom))

            assertEquals(
                SearchState(
                    input = TextInput(value = "bloating", validationError = null),
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

        viewModel.handleEvent(SymptomEntryEvent.UpdateSelectedSeverity(Severity.MILD))
        viewModel.handleEvent(SymptomEntryEvent.UpdateSelectedSearchSymptom(symptom))

        assertEquals(listOf<SymptomWithSeverity>(), viewModel.uiState.selectedSymptoms)

        // Adding a symptom
        viewModel.handleEvent(SymptomEntryEvent.AddSymptomWithSeverity)

        // Asserting that the symptom is added and the search state and selected severity are reset
        assertEquals(
            listOf(SymptomWithSeverity(symptom = symptom, severity = Severity.MILD)),
            viewModel.uiState.selectedSymptoms
        )
        assertEquals(
            SearchState(
                input = TextInput(value = "", validationError = null),
                selectedSymptom = null,
                results = listOf(symptom),
                canCreateNewSymptom = false,
            ),
            viewModel.uiState.searchState
        )
        assertNull(viewModel.uiState.selectedSeverity)

        // Removing the symptom
        viewModel.handleEvent(
            SymptomEntryEvent.RemoveSymptom(
                SymptomWithSeverity(
                    symptom = symptom,
                    severity = Severity.MILD
                )
            )
        )

        // Asserting that the selected symptoms is empty again
        assertEquals(listOf<SymptomWithSeverity>(), viewModel.uiState.selectedSymptoms)

        collectJob.cancel()
    }

    @Test
    fun dateInputFieldsUpdate_whenUpdateDateIsCalled() = runTest {
        val date = LocalDate.of(2024, 2, 1)

        viewModel.handleEvent(SymptomEntryEvent.UpdateDate(date))

        assertEquals(date, viewModel.uiState.dateTimeInput.date)
    }

    @Test
    fun timeInputFieldsUpdate_whenUpdateTimeIsCalled() = runTest {
        val time = LocalTime.of(10, 25)

        viewModel.handleEvent(SymptomEntryEvent.UpdateTime(time))

        assertEquals(time, viewModel.uiState.dateTimeInput.time)
    }
}

class TestSymptomEntryViewModel(symptomRepository: SymptomRepository) :
    AbstractSymptomEntryViewModel(symptomRepository = symptomRepository) {
    override fun submit() {}
}
