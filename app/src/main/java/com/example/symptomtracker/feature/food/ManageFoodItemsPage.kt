package com.example.symptomtracker.feature.food

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.symptomtracker.R
import com.example.symptomtracker.core.designsystem.SymptomTrackerTheme
import com.example.symptomtracker.core.designsystem.component.Dialog
import com.example.symptomtracker.core.designsystem.component.LabelledOutlinedReadOnlyDropdown
import com.example.symptomtracker.core.designsystem.component.LabelledOutlinedTextField
import com.example.symptomtracker.core.designsystem.icon.DeleteIcon
import com.example.symptomtracker.core.designsystem.icon.EditIcon
import com.example.symptomtracker.core.domain.model.FoodItem
import com.example.symptomtracker.core.ui.ItemPreviewParameterProvider
import com.example.symptomtracker.ui.SymptomTrackerTopAppBar

@Composable
fun ManageFoodItemsRoute(
    navigateBack: () -> Unit,
    viewModel: ManageItemsViewModel = hiltViewModel(),
) {
    val foodItemsState by viewModel.foodItemsState.collectAsState()
    val userActionState by viewModel.userActionState.collectAsState()

    ManageFoodItemsPage(
        navigateBack = navigateBack,
        foodItemsState = foodItemsState,
        userActionState = userActionState,
        eventSink = viewModel::handleEvent,
    )
}

@Composable
internal fun ManageFoodItemsPage(
    navigateBack: () -> Unit,
    foodItemsState: FoodItemsUiState,
    userActionState: ActionState?,
    eventSink: (ManageFoodEvent) -> Unit,
) {
    Scaffold(
        topBar = {
            SymptomTrackerTopAppBar(
                title = stringResource(id = R.string.manage_food_items_title),
                canNavigateBack = true,
                navigateUp = navigateBack,
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            when (foodItemsState) {
                is FoodItemsUiState.Loading -> {}
                is FoodItemsUiState.Data -> Column {
                    if (foodItemsState.items.isEmpty()) {
                        Text(text = stringResource(id = R.string.no_items_found))
                    }

                    LazyColumn {
                        items(items = foodItemsState.items) { item ->
                            FoodItemRow(
                                foodItem = item,
                                onActionChosen = { foodItem, action ->
                                    eventSink(
                                        ManageFoodEvent.StartAction(
                                            foodItem = foodItem,
                                            action = action
                                        )
                                    )
                                },
                            )
                        }
                    }
                }
            }

            when (userActionState) {
                is ActionState.Edit ->
                    EditDialog(
                        state = userActionState,
                        onEditNameChange = { eventSink(ManageFoodEvent.UpdateName(it)) },
                        onSubmit = { eventSink(ManageFoodEvent.SubmitAction) },
                        onClose = { eventSink(ManageFoodEvent.CancelAction) },
                    )

                is ActionState.Delete.Direct ->
                    DirectDeleteDialog(
                        state = userActionState,
                        onSubmit = { eventSink(ManageFoodEvent.SubmitAction) },
                        onClose = { eventSink(ManageFoodEvent.CancelAction) },
                    )

                is ActionState.Delete.Merge ->
                    MergeDeleteDialog(
                        state = userActionState,
                        onSelectedItemUpdated = { eventSink(ManageFoodEvent.ChooseMergeCandidate(it)) },
                        onSubmit = { eventSink(ManageFoodEvent.SubmitAction) },
                        onClose = { eventSink(ManageFoodEvent.CancelAction) },
                    )

                null -> {}
            }
        }
    }
}

@Composable
fun FoodItemRow(
    foodItem: FoodItem,
    onActionChosen: (FoodItem, FoodItemAction) -> Unit,
) {
    var actionMenuOpen by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = { Text(text = foodItem.name) },
        trailingContent = {
            IconButton(onClick = { actionMenuOpen = true }) {
                Icon(
                    painter = painterResource(R.drawable.outline_more_vert_24),
                    contentDescription = stringResource(
                        id = R.string.manage_food_items_options_menu_cd,
                        formatArgs = arrayOf(foodItem.name),
                    )
                )
            }
            DropdownMenu(expanded = actionMenuOpen, onDismissRequest = { actionMenuOpen = false }) {
                DropdownMenuItem(
                    text = { Text(text = stringResource(id = R.string.action_edit)) },
                    onClick = {
                        onActionChosen(foodItem, FoodItemAction.EDIT)
                        actionMenuOpen = false
                    },
                    leadingIcon = {
                        EditIcon(
                            contentDescription = stringResource(
                                id = R.string.manage_food_items_edit,
                                formatArgs = arrayOf(foodItem.name),
                            )
                        )
                    }
                )
                DropdownMenuItem(
                    text = { Text(text = stringResource(id = R.string.action_delete)) },
                    onClick = {
                        onActionChosen(foodItem, FoodItemAction.DELETE)
                        actionMenuOpen = false
                    },
                    leadingIcon = {
                        DeleteIcon(
                            contentDescription = stringResource(
                                id = R.string.manage_food_items_delete,
                                formatArgs = arrayOf(foodItem.name),
                            )
                        )
                    }
                )
            }
        }
    )
}

@Composable
internal fun EditDialog(
    state: ActionState.Edit,
    onEditNameChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onClose: () -> Unit,
) {
    Dialog(
        title = stringResource(R.string.manage_food_items_edit).format(state.foodItem.name),
        confirmButtonText = R.string.action_save,
        confirmButtonEnabled = state.canSubmit,
        icon = {
            EditIcon(
                contentDescription = stringResource(
                    id = R.string.manage_food_items_edit,
                    formatArgs = arrayOf(state.foodItem.name),
                )
            )
        },
        onSubmit = onSubmit,
        onClose = onClose
    ) {
        LabelledOutlinedTextField(
            label = stringResource(id = R.string.name_input_label),
            value = state.name,
            onValueChange = { onEditNameChange(it) },
        )
    }
}

@Composable
internal fun DirectDeleteDialog(
    state: ActionState.Delete.Direct,
    onSubmit: () -> Unit,
    onClose: () -> Unit,
) {
    Dialog(
        title = stringResource(R.string.manage_food_items_delete).format(state.foodItem.name),
        confirmButtonText = R.string.action_delete,
        icon = {
            DeleteIcon(
                contentDescription = stringResource(
                    id = R.string.manage_food_items_delete,
                    formatArgs = arrayOf(state.foodItem.name),
                )
            )
        },
        onSubmit = onSubmit,
        onClose = onClose,
    ) {
        Text(text = stringResource(id = R.string.manage_food_items_direct_delete_dialog))
    }
}

@Composable
internal fun MergeDeleteDialog(
    state: ActionState.Delete.Merge,
    onSelectedItemUpdated: (FoodItem) -> Unit,
    onSubmit: () -> Unit,
    onClose: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Dialog(
        title = stringResource(R.string.manage_food_items_merge).format(state.foodItem.name),
        confirmButtonText = R.string.action_merge,
        confirmButtonEnabled = state.canSubmit,
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.outline_merge_24),
                contentDescription = stringResource(
                    id = R.string.manage_food_items_merge,
                    formatArgs = arrayOf(state.foodItem.name),
                )
            )
        },
        onSubmit = onSubmit,
        onClose = onClose
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(text = stringResource(id = R.string.manage_food_items_merge_delete_dialog))

            LabelledOutlinedReadOnlyDropdown(
                label = stringResource(id = R.string.manage_food_items_merge_delete_selection_label),
                value = state.chosenItem?.name ?: "",
                expanded = expanded,
                onExpandedChange = { expanded = it },
                modifier = Modifier.fillMaxWidth(),
            ) {
                state.mergeCandidates.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item.name) },
                        onClick = {
                            onSelectedItemUpdated(item)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ManageFoodItemsPagePreview(@PreviewParameter(ItemPreviewParameterProvider::class) foodItems: List<FoodItem>) {
    SymptomTrackerTheme {
        ManageFoodItemsPage(
            navigateBack = {},
            foodItemsState = FoodItemsUiState.Data(foodItems),
            userActionState = null,
            eventSink = {},
        )
    }
}

@Preview
@Composable
fun EditDialogPreview(@PreviewParameter(EditActionStateProvider::class) state: ActionState.Edit) {
    SymptomTrackerTheme {
        EditDialog(
            state = state,
            onEditNameChange = {},
            onSubmit = {},
            onClose = {},
        )
    }
}

@Preview
@Composable
fun DirectDeleteDialogPreview() {
    SymptomTrackerTheme {
        DirectDeleteDialog(
            state = ActionState.Delete.Direct(foodItem = FoodItem(id = 1, name = "Oats")),
            onSubmit = { },
            onClose = { },
        )
    }
}

@Preview
@Composable
fun MergeDeleteDialogPreview(@PreviewParameter(MergeDeleteActionStateProvider::class) state: ActionState.Delete.Merge) {
    SymptomTrackerTheme {
        MergeDeleteDialog(
            state = state,
            onSelectedItemUpdated = { },
            onSubmit = { },
            onClose = { },
        )
    }
}
