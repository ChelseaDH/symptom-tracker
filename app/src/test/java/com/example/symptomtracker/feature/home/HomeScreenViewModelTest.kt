package com.example.symptomtracker.feature.home

import com.example.symptomtracker.core.model.FoodItem
import com.example.symptomtracker.core.model.FoodLog
import com.example.symptomtracker.core.model.MovementLog
import com.example.symptomtracker.core.model.Severity
import com.example.symptomtracker.core.model.StoolType
import com.example.symptomtracker.core.model.Symptom
import com.example.symptomtracker.core.model.SymptomLog
import com.example.symptomtracker.core.model.SymptomWithSeverity
import com.example.symptomtracker.core.testing.repository.TestFoodRepository
import com.example.symptomtracker.core.testing.repository.TestMovementRepository
import com.example.symptomtracker.core.testing.repository.TestSettingsRepository
import com.example.symptomtracker.core.testing.repository.TestSymptomRepository
import com.example.symptomtracker.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import java.time.OffsetDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class HomeScreenViewModelTest {
    @get:Rule
    val dispatcherRule: TestWatcher = MainDispatcherRule()

    private val foodLogRepository = TestFoodRepository()
    private val symptomRepository = TestSymptomRepository()
    private val movementRepository = TestMovementRepository()
    private val settingsRepository = TestSettingsRepository()
    private lateinit var viewModel: HomeScreenViewModel

    @Before
    fun setup() {
        viewModel = HomeScreenViewModel(
            foodLogRepository = foodLogRepository,
            symptomRepository = symptomRepository,
            movementRepository = movementRepository,
            settingsRepository = settingsRepository,
        )
    }

    @Test
    fun uiState_holdsDefaultValues_whenInitialised() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState }

        foodLogRepository.sendFoodLogs(foodLogs)
        symptomRepository.sendSymptomLogs(symptomLogs)
        movementRepository.sendMovementLogs(movementLogs)

        assertFalse(viewModel.uiState.showBottomSheet)
        assertEquals(listOf(foodLogs[1], symptomLogs[0]), viewModel.uiState.logs)
        assertTrue(viewModel.uiState.isToday)

        collectJob.cancel()
    }

    @Test
    fun bottomSheetVisibilityUpdates_whenUpdateBottomSheetVisibilityIsCalled() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState }

        assertFalse(viewModel.uiState.showBottomSheet)

        viewModel.updateBottomSheetVisibility(true)

        assertTrue(viewModel.uiState.showBottomSheet)

        collectJob.cancel()
    }

    @Test
    fun dateUpdates_whenUpdateDateIsCalled() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState }

        assertTrue(viewModel.uiState.isToday)

        viewModel.updateDate(1716379200000)

        assertFalse(viewModel.uiState.isToday)
        assertEquals(1716379200, viewModel.uiState.date.toEpochSecond())

        collectJob.cancel()
    }

    @Test
    fun dateAndLogsUpdate_whenPreviousAndNextDayAreChosen() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState }

        foodLogRepository.sendFoodLogs(foodLogs)
        symptomRepository.sendSymptomLogs(symptomLogs)
        movementRepository.sendMovementLogs(movementLogs)

        assertTrue(viewModel.uiState.isToday)
        assertEquals(listOf(foodLogs[1], symptomLogs[0]), viewModel.uiState.logs)

        viewModel.goToPreviousDay()

        assertFalse(viewModel.uiState.isToday)
        assertEquals(listOf(foodLogs[0], movementLogs[0]), viewModel.uiState.logs)

        viewModel.goToNextDay()

        assertTrue(viewModel.uiState.isToday)
        assertEquals(listOf(foodLogs[1], symptomLogs[0]), viewModel.uiState.logs)

        collectJob.cancel()
    }

    @Test
    fun whenInitialised_mealieIntegrationEnabledHoldsDefaultValue() = runTest {
        assertFalse(viewModel.mealieIntegrationEnabled.value)
    }

    @Test
    fun whenSettingsAreRetrieved_mealieIntegrationEnabledHoldsExpectedValue() = runTest {
        val collectJob =
            launch(UnconfinedTestDispatcher()) { viewModel.mealieIntegrationEnabled.collect() }

        settingsRepository.sendMealieEnabled(true)

        assertTrue(viewModel.mealieIntegrationEnabled.value)

        collectJob.cancel()
    }
}

val foodLogs = listOf(
    FoodLog(
        id = 1,
        date = OffsetDateTime.now().minusDays(1),
        items = listOf(FoodItem(id = 1, name = "oats"))
    ),
    FoodLog(
        id = 1,
        date = OffsetDateTime.now(),
        items = listOf(FoodItem(id = 2, name = "blueberries"))
    ),
)

val symptomLogs = listOf(
    SymptomLog(
        id = 1,
        date = OffsetDateTime.now(),
        items = listOf(
            SymptomWithSeverity(
                symptom = Symptom(id = 1, name = "bloating"),
                severity = Severity.MILD
            )
        )
    )
)

val movementLogs = listOf(
    MovementLog(
        id = 1,
        date = OffsetDateTime.now().minusDays(1),
        stoolType = StoolType.NORMAL_3
    )
)
