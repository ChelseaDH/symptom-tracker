package com.example.symptomtracker.feature.symptom

import androidx.lifecycle.SavedStateHandle
import com.example.symptomtracker.core.domain.model.Severity
import com.example.symptomtracker.core.domain.model.Symptom
import com.example.symptomtracker.core.domain.model.SymptomLog
import com.example.symptomtracker.core.domain.model.SymptomWithSeverity
import com.example.symptomtracker.core.testing.repository.TestSymptomRepository
import com.example.symptomtracker.core.ui.ViewLogUiState
import com.example.symptomtracker.feature.symptom.navigation.SYMPTOM_LOG_ID
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

class ViewSymptomViewModelTest {
    @get:Rule
    val dispatcherRule: TestWatcher = MainDispatcherRule()

    private val symptomRepository = TestSymptomRepository()
    private lateinit var viewModel: ViewSymptomViewModel

    @Before
    fun setup() {
        viewModel = ViewSymptomViewModel(
            savedStateHandle = SavedStateHandle(mapOf(SYMPTOM_LOG_ID to 1)),
            symptomRepository = symptomRepository,
        )
    }

    @Test
    fun logId_matchesSymptomLogIdFromSavedStateHandle() = assertEquals(1, viewModel.logId)

    @Test
    fun uiState_isLoading_whenFirstInitialised() = runTest {
        assertEquals(ViewLogUiState.Loading, viewModel.uiState.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun uiState_isEmpty_whenSymptomLogDoesNotExist() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        symptomRepository.sendSymptomLogs(listOf())

        assertEquals(ViewLogUiState.Empty, viewModel.uiState.value)

        collectJob.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun uiState_holdsData_whenSymptomLogExists() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        val symptomLog =
            SymptomLog(
                id = 1,
                date = OffsetDateTime.parse("2023-03-02T09:10:00+00:00"),
                items = listOf(
                    SymptomWithSeverity(
                        symptom = Symptom(id = 1, name = "bloating"),
                        severity = Severity.MILD
                    )
                ),
            )
        symptomRepository.sendSymptomLogs(listOf(symptomLog))

        assertEquals(ViewLogUiState.Data(symptomLog), viewModel.uiState.value)

        collectJob.cancel()
    }
}
