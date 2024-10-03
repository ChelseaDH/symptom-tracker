package com.example.symptomtracker.feature.food

import com.example.symptomtracker.core.domain.model.FoodItem
import com.example.symptomtracker.core.domain.model.FoodLog
import com.example.symptomtracker.core.testing.repository.TestFoodRepository
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

class ManageItemsViewModelTest {
    @get:Rule
    val dispatcherRule: TestWatcher = MainDispatcherRule()

    private val foodLogRepository = TestFoodRepository()
    private lateinit var viewModel: ManageItemsViewModel

    @Before
    fun setup() {
        viewModel = ManageItemsViewModel(foodLogRepository = foodLogRepository)
    }

    @Test
    fun foodItemsState_isLoading_whenFirstInitialised() = runTest {
        assertEquals(FoodItemsUiState.Loading, viewModel.foodItemsState.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun foodItemsState_holdsData_whenFoodItemsExist() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.foodItemsState.collect() }
        val foodItems = listOf(
            FoodItem(id = 1, name = "oats"),
            FoodItem(id = 2, name = "blueberries"),
        )
        foodLogRepository.sendFoodItems(foodItems)

        assertEquals(FoodItemsUiState.Data(foodItems), viewModel.foodItemsState.value)

        collectJob.cancel()
    }

    @Test
    fun userActionState_isNull_whenFirstInitialised() = runTest {
        assertNull(viewModel.userActionState.value)
    }

    @Test
    fun cancelAction_setsUserActionStateToNull() = runTest {
        val foodItem = FoodItem(id = 1, name = "oats")

        viewModel.handleEvent(
            ManageFoodEvent.StartAction(
                foodItem = foodItem,
                action = FoodItemAction.EDIT
            )
        )
        assertEquals(
            ActionState.Edit(foodItem = foodItem, name = foodItem.name),
            viewModel.userActionState.value
        )

        viewModel.handleEvent(ManageFoodEvent.CancelAction)
        assertNull(viewModel.userActionState.value)
    }

    @Test
    fun onEditNameChange_updatesNameInEditActionState() = runTest {
        val foodItem = FoodItem(id = 1, name = "oats")

        viewModel.handleEvent(
            ManageFoodEvent.StartAction(
                foodItem = foodItem,
                action = FoodItemAction.EDIT
            )
        )
        assertEquals(
            ActionState.Edit(foodItem = foodItem, name = foodItem.name),
            viewModel.userActionState.value
        )

        viewModel.handleEvent(ManageFoodEvent.UpdateName(name = "blueberries"))
        assertEquals(
            ActionState.Edit(foodItem = foodItem, name = "blueberries", canSubmit = true),
            viewModel.userActionState.value
        )

        viewModel.handleEvent(ManageFoodEvent.UpdateName(name = ""))
        assertEquals(
            ActionState.Edit(foodItem = foodItem, name = "", canSubmit = false),
            viewModel.userActionState.value
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun editAction_isStartedUpdatedAndSubmittedSuccessfully() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.foodItemsState.collect() }

        val foodItem = FoodItem(id = 1, name = "oats")
        foodLogRepository.sendFoodItems(listOf(foodItem))

        viewModel.handleEvent(
            ManageFoodEvent.StartAction(
                foodItem = foodItem,
                action = FoodItemAction.EDIT
            )
        )
        viewModel.handleEvent(ManageFoodEvent.UpdateName("blueberries"))
        assertEquals(
            ActionState.Edit(foodItem = foodItem, name = "blueberries", canSubmit = true),
            viewModel.userActionState.value
        )

        viewModel.handleEvent(ManageFoodEvent.SubmitAction)
        assertEquals(
            FoodItemsUiState.Data(listOf(FoodItem(id = 1, name = "blueberries"))),
            viewModel.foodItemsState.value
        )
        assertNull(viewModel.userActionState.value)

        collectJob.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun directDeleteAction_isStartedAndSubmittedSuccessfully() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.foodItemsState.collect() }

        val foodItems = listOf(
            FoodItem(id = 1, name = "oats"),
            FoodItem(id = 2, name = "blueberries"),
        )
        foodLogRepository.sendFoodItems(foodItems)
        foodLogRepository.sendFoodLogs(emptyList())

        viewModel.handleEvent(
            ManageFoodEvent.StartAction(
                foodItem = foodItems[0],
                action = FoodItemAction.DELETE
            )
        )
        assertEquals(
            ActionState.Delete.Direct(foodItem = foodItems[0]),
            viewModel.userActionState.value
        )

        viewModel.handleEvent(ManageFoodEvent.SubmitAction)
        assertEquals(
            FoodItemsUiState.Data(listOf(foodItems[1])),
            viewModel.foodItemsState.value
        )
        assertNull(viewModel.userActionState.value)

        collectJob.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun mergeDeleteAction_isStartedAndSubmittedSuccessfully() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.foodItemsState.collect() }

        val foodItems = listOf(
            FoodItem(id = 1, name = "oats"),
            FoodItem(id = 2, name = "blueberries"),
        )
        foodLogRepository.sendFoodItems(foodItems)
        foodLogRepository.sendFoodLogs(
            listOf(
                FoodLog(
                    id = 1,
                    date = OffsetDateTime.now(),
                    items = foodItems
                )
            )
        )

        viewModel.handleEvent(
            ManageFoodEvent.StartAction(
                foodItem = foodItems[0],
                action = FoodItemAction.DELETE
            )
        )
        assertEquals(
            ActionState.Delete.Merge(
                foodItem = foodItems[0],
                mergeCandidates = listOf(foodItems[1]),
                chosenItem = null,
                canSubmit = false,
            ),
            viewModel.userActionState.value
        )

        viewModel.handleEvent(ManageFoodEvent.ChooseMergeCandidate(chosenItem = foodItems[1]))
        assertEquals(
            ActionState.Delete.Merge(
                foodItem = foodItems[0],
                mergeCandidates = listOf(foodItems[1]),
                chosenItem = foodItems[1],
                canSubmit = true,
            ),
            viewModel.userActionState.value
        )

        viewModel.handleEvent(ManageFoodEvent.SubmitAction)
        assertEquals(
            FoodItemsUiState.Data(listOf(foodItems[1])),
            viewModel.foodItemsState.value
        )
        assertNull(viewModel.userActionState.value)

        collectJob.cancel()
    }
}
