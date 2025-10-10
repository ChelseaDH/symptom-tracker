package com.example.symptomtracker.feature.drink

import com.example.symptomtracker.core.domain.model.DrinkItem
import com.example.symptomtracker.core.domain.model.DrinkLog
import com.example.symptomtracker.core.testing.repository.TestDrinkRepository
import com.example.symptomtracker.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import java.time.OffsetDateTime

class ManageDrinkItemsViewModelTest {
    @get:Rule
    val dispatcherRule: TestWatcher = MainDispatcherRule()

    private val drinkLogRepository = TestDrinkRepository()
    private lateinit var viewModel: ManageDrinkItemsViewModel

    @Before
    fun setup() {
        viewModel = ManageDrinkItemsViewModel(drinkLogRepository = drinkLogRepository)
    }

    @Test
    fun drinkItemsState_isLoading_whenFirstInitialised() = runTest {
        assertEquals(DrinkItemsUiState.Loading, viewModel.drinkItemsState.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun drinkItemsState_holdsData_whenDrinkItemsExist() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.drinkItemsState.collect() }
        val drinkItems = listOf(
            DrinkItem(id = 1, name = "water"),
            DrinkItem(id = 2, name = "coffee"),
        )
        drinkLogRepository.sendDrinkItems(drinkItems)

        assertEquals(DrinkItemsUiState.Data(drinkItems), viewModel.drinkItemsState.value)

        collectJob.cancel()
    }

    @Test
    fun userActionState_isNull_whenFirstInitialised() = runTest {
        assertNull(viewModel.userActionState.value)
    }

    @Test
    fun cancelAction_setsUserActionStateToNull() = runTest {
        val drinkItem = DrinkItem(id = 1, name = "water")

        viewModel.handleEvent(
            ManageDrinkEvent.StartAction(
                drinkItem = drinkItem,
                action = DrinkItemAction.EDIT
            )
        )
        assertEquals(
            DrinkActionState.Edit(drinkItem = drinkItem, name = drinkItem.name),
            viewModel.userActionState.value
        )

        viewModel.handleEvent(ManageDrinkEvent.CancelAction)
        assertNull(viewModel.userActionState.value)
    }

    @Test
    fun onEditNameChange_updatesNameInEditActionState() = runTest {
        val drinkItem = DrinkItem(id = 1, name = "water")

        viewModel.handleEvent(
            ManageDrinkEvent.StartAction(
                drinkItem = drinkItem,
                action = DrinkItemAction.EDIT
            )
        )
        assertEquals(
            DrinkActionState.Edit(drinkItem = drinkItem, name = drinkItem.name),
            viewModel.userActionState.value
        )

        viewModel.handleEvent(ManageDrinkEvent.UpdateName(name = "coffee"))
        assertEquals(
            DrinkActionState.Edit(drinkItem = drinkItem, name = "coffee", canSubmit = true),
            viewModel.userActionState.value
        )

        viewModel.handleEvent(ManageDrinkEvent.UpdateName(name = ""))
        assertEquals(
            DrinkActionState.Edit(drinkItem = drinkItem, name = "", canSubmit = false),
            viewModel.userActionState.value
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun editAction_isStartedUpdatedAndSubmittedSuccessfully() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.drinkItemsState.collect() }

        val drinkItem = DrinkItem(id = 1, name = "water")
        drinkLogRepository.sendDrinkItems(listOf(drinkItem))

        viewModel.handleEvent(
            ManageDrinkEvent.StartAction(
                drinkItem = drinkItem,
                action = DrinkItemAction.EDIT
            )
        )
        viewModel.handleEvent(ManageDrinkEvent.UpdateName("coffee"))
        assertEquals(
            DrinkActionState.Edit(drinkItem = drinkItem, name = "coffee", canSubmit = true),
            viewModel.userActionState.value
        )

        viewModel.handleEvent(ManageDrinkEvent.SubmitAction)
        assertEquals(
            DrinkItemsUiState.Data(listOf(DrinkItem(id = 1, name = "coffee"))),
            viewModel.drinkItemsState.value
        )
        assertNull(viewModel.userActionState.value)

        collectJob.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun directDeleteAction_isStartedAndSubmittedSuccessfully() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.drinkItemsState.collect() }

        val drinkItems = listOf(
            DrinkItem(id = 1, name = "water"),
            DrinkItem(id = 2, name = "coffee"),
        )
        drinkLogRepository.sendDrinkItems(drinkItems)
        drinkLogRepository.sendDrinkLogs(emptyList())

        viewModel.handleEvent(
            ManageDrinkEvent.StartAction(
                drinkItem = drinkItems[0],
                action = DrinkItemAction.DELETE
            )
        )
        assertEquals(
            DrinkActionState.Delete.Direct(drinkItem = drinkItems[0]),
            viewModel.userActionState.value
        )

        viewModel.handleEvent(ManageDrinkEvent.SubmitAction)
        assertEquals(
            DrinkItemsUiState.Data(listOf(drinkItems[1])),
            viewModel.drinkItemsState.value
        )
        assertNull(viewModel.userActionState.value)

        collectJob.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun mergeDeleteAction_isStartedAndSubmittedSuccessfully() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.drinkItemsState.collect() }

        val drinkItems = listOf(
            DrinkItem(id = 1, name = "water"),
            DrinkItem(id = 2, name = "coffee"),
        )
        drinkLogRepository.sendDrinkItems(drinkItems)
        drinkLogRepository.sendDrinkLogs(
            listOf(
                DrinkLog(
                    id = 1,
                    date = OffsetDateTime.now(),
                    items = drinkItems
                )
            )
        )

        viewModel.handleEvent(
            ManageDrinkEvent.StartAction(
                drinkItem = drinkItems[0],
                action = DrinkItemAction.DELETE
            )
        )
        assertEquals(
            DrinkActionState.Delete.Merge(
                drinkItem = drinkItems[0],
                mergeCandidates = listOf(drinkItems[1]),
                chosenItem = null,
                canSubmit = false,
            ),
            viewModel.userActionState.value
        )

        viewModel.handleEvent(ManageDrinkEvent.ChooseMergeCandidate(chosenItem = drinkItems[1]))
        assertEquals(
            DrinkActionState.Delete.Merge(
                drinkItem = drinkItems[0],
                mergeCandidates = listOf(drinkItems[1]),
                chosenItem = drinkItems[1],
                canSubmit = true,
            ),
            viewModel.userActionState.value
        )

        viewModel.handleEvent(ManageDrinkEvent.SubmitAction)
        assertEquals(
            DrinkItemsUiState.Data(listOf(drinkItems[1])),
            viewModel.drinkItemsState.value
        )
        assertNull(viewModel.userActionState.value)

        collectJob.cancel()
    }
}
