package com.example.symptomtracker.feature.logs

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
import org.junit.Assert
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import java.time.OffsetDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class LogsViewModelTest {
    @get:Rule
    val dispatcherRule: TestWatcher = MainDispatcherRule()

    private val foodLogRepository = TestFoodRepository()
    private val symptomRepository = TestSymptomRepository()
    private val movementRepository = TestMovementRepository()
    private val settingsRepository = TestSettingsRepository()
    private lateinit var viewModel: LogsViewModel

    @Before
    fun setup() {
        viewModel = LogsViewModel(
            foodLogRepository = foodLogRepository,
            symptomRepository = symptomRepository,
            movementRepository = movementRepository,
            settingsRepository = settingsRepository,
        )
    }

    @Test
    fun uiState_holdsDefaultValues_whenInitialised() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState }

        Assert.assertEquals(0, viewModel.uiState.selectedTabIndex)
        Assert.assertEquals(TabUiState.Loading, viewModel.uiState.tabState)

        collectJob.cancel()
    }

    @Test
    fun uiState_holdsFoodLogs_afterInitialisation() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState }

        foodLogRepository.sendFoodLogs(foodLogs)
        symptomRepository.sendSymptomLogs(symptomLogs)
        movementRepository.sendMovementLogs(movementLogs)

        Assert.assertEquals(0, viewModel.uiState.selectedTabIndex)
        Assert.assertEquals(TabUiState.FoodLogs(foodLogs), viewModel.uiState.tabState)

        collectJob.cancel()
    }

    @Test
    fun uiState_holdsCorrectLogs_whenChosenTabUpdated() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState }

        foodLogRepository.sendFoodLogs(foodLogs)
        symptomRepository.sendSymptomLogs(symptomLogs)
        movementRepository.sendMovementLogs(movementLogs)

        Assert.assertEquals(0, viewModel.uiState.selectedTabIndex)
        Assert.assertEquals(TabUiState.FoodLogs(foodLogs), viewModel.uiState.tabState)

        viewModel.goToNextTab()
        Assert.assertEquals(1, viewModel.uiState.selectedTabIndex)
        Assert.assertEquals(TabUiState.SymptomLogs(symptomLogs), viewModel.uiState.tabState)

        viewModel.goToPreviousTab()
        Assert.assertEquals(0, viewModel.uiState.selectedTabIndex)
        Assert.assertEquals(TabUiState.FoodLogs(foodLogs), viewModel.uiState.tabState)

        viewModel.updateSelectedTab(2)
        Assert.assertEquals(2, viewModel.uiState.selectedTabIndex)
        Assert.assertEquals(TabUiState.MovementLogs(movementLogs), viewModel.uiState.tabState)

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
