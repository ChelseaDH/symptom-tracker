package com.example.symptomtracker.feature.symptom

import androidx.lifecycle.SavedStateHandle
import com.example.symptomtracker.core.model.Severity
import com.example.symptomtracker.core.model.Symptom
import com.example.symptomtracker.core.model.SymptomLog
import com.example.symptomtracker.core.model.SymptomWithSeverity
import com.example.symptomtracker.core.testing.repository.TestSymptomRepository
import com.example.symptomtracker.core.ui.DateInputFields
import com.example.symptomtracker.core.ui.DateTimeInput
import com.example.symptomtracker.core.ui.TimeInputFields
import com.example.symptomtracker.feature.symptom.navigation.SYMPTOM_LOG_ID
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

class EditSymptomViewModelTest {
    @get:Rule
    val dispatcherRule: TestWatcher = MainDispatcherRule()

    private val symptomRepository = TestSymptomRepository()
    private lateinit var viewModel: EditSymptomViewModel

    @Before
    fun setup() {
        viewModel = EditSymptomViewModel(
            savedStateHandle = SavedStateHandle(mapOf(SYMPTOM_LOG_ID to 1)),
            symptomRepository = symptomRepository,
        )
    }

    @Test
    fun logId_matchesSymptomLogIdFromSavedStateHandle() = assertEquals(1, viewModel.logId)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun uiState_holdsDefaultValues_whenSymptomLogDoesNotExist() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState }

        symptomRepository.sendSymptomLogs(listOf())

        assertEquals(listOf<SymptomWithSeverity>(), viewModel.uiState.selectedSymptoms)

        collectJob.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun uiState_holdsLogValues_whenSymptomLogExists() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState }

        val symptom = Symptom(id = 1, name = "bloating")
        symptomRepository.sendSymptoms(listOf(symptom))

        val symptomLogSymptoms = listOf(
            SymptomWithSeverity(symptom = symptom, severity = Severity.MILD)
        )
        val symptomLog = SymptomLog(
            id = 1,
            date = OffsetDateTime.parse("2023-03-02T09:10:00+00:00"),
            items = symptomLogSymptoms,
        )
        symptomRepository.sendSymptomLogs(listOf(symptomLog))

        assertEquals(
            SymptomEntryUiState(
                selectedSymptoms = symptomLogSymptoms,
                dateTimeInput = DateTimeInput(
                    dateInputFields = DateInputFields(year = 2023, month = 2, day = 2),
                    timeInputFields = TimeInputFields(hour = 9, minute = 10)
                ),
                searchState = SearchState(
                    input = "",
                    selectedSymptom = null,
                    results = listOf(symptom),
                    canCreateNewSymptom = false,
                ),
                selectedSeverity = null,
            ), viewModel.uiState
        )

        collectJob.cancel()
    }
}
