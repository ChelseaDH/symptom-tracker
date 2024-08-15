package com.example.symptomtracker.feature.food

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
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
import com.example.symptomtracker.core.model.FoodItem
import com.example.symptomtracker.core.ui.Dialog
import com.example.symptomtracker.core.ui.ItemPreviewParameterProvider
import com.example.symptomtracker.core.ui.SymptomTrackerTheme
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
        onActionChosen = viewModel::startAction,
        onEditNameChange = viewModel::onEditNameChange,
        onMergeCandidateChosen = viewModel::onMergeCandidateChosen,
        onSubmitAction = viewModel::submitAction,
        onCancelAction = viewModel::cancelAction,
    )
}

@Composable
internal fun ManageFoodItemsPage(
    navigateBack: () -> Unit,
    foodItemsState: FoodItemsUiState,
    userActionState: ActionState?,
    onActionChosen: (FoodItem, FoodItemAction) -> Unit,
    onEditNameChange: (String) -> Unit,
    onMergeCandidateChosen: (FoodItem) -> Unit,
    onSubmitAction: () -> Unit,
    onCancelAction: () -> Unit,
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
                                onActionChosen = onActionChosen,
                            )
                        }
                    }
                }
            }

            when (userActionState) {
                is ActionState.Edit ->
                    EditDialog(
                        state = userActionState,
                        onEditNameChange = onEditNameChange,
                        onSubmit = onSubmitAction,
                        onClose = onCancelAction,
                    )

                is ActionState.Delete.Direct ->
                    DirectDeleteDialog(
                        state = userActionState,
                        onSubmit = onSubmitAction,
                        onClose = onCancelAction,
                    )

                is ActionState.Delete.Merge ->
                    MergeDeleteDialog(
                        state = userActionState,
                        onSelectedItemUpdated = onMergeCandidateChosen,
                        onSubmit = onSubmitAction,
                        onClose = onCancelAction,
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
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(
                        id = R.string.manage_food_items_options_menu_cd,
                        formatArgs = arrayOf(foodItem.name)
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
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(
                                id = R.string.manage_food_items_edit_cd,
                                formatArgs = arrayOf(foodItem.name)
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
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(
                                id = R.string.manage_food_items_delete_cd,
                                formatArgs = arrayOf(foodItem.name)
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
        title = "Edit %s".format(state.foodItem.name),
        confirmButtonText = R.string.action_save,
        confirmButtonEnabled = state.canSubmit,
        icon = {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(
                    id = R.string.manage_food_items_edit_cd,
                    formatArgs = arrayOf(state.foodItem.name)
                )
            )
        },
        onSubmit = onSubmit,
        onClose = onClose
    ) {
        OutlinedTextField(
            value = state.name,
            onValueChange = { onEditNameChange(it) },
            label = { Text(text = stringResource(id = R.string.name_input_label)) },
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
        title = "Delete %s".format(state.foodItem.name),
        confirmButtonText = R.string.action_delete,
        icon = {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(
                    id = R.string.manage_food_items_delete_cd,
                    formatArgs = arrayOf(state.foodItem.name)
                )
            )
        },
        onSubmit = onSubmit,
        onClose = onClose
    ) {
        Text(text = stringResource(id = R.string.manage_food_items_direct_delete_dialog))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MergeDeleteDialog(
    state: ActionState.Delete.Merge,
    onSelectedItemUpdated: (FoodItem) -> Unit,
    onSubmit: () -> Unit,
    onClose: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Dialog(
        title = "Merge %s".format(state.foodItem.name),
        confirmButtonText = R.string.action_merge,
        confirmButtonEnabled = state.canSubmit,
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.outline_merge_24),
                contentDescription = stringResource(
                    id = R.string.manage_food_items_merge_cd,
                    formatArgs = arrayOf(state.foodItem.name)
                )
            )
        },
        onSubmit = onSubmit,
        onClose = onClose
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = stringResource(id = R.string.manage_food_items_merge_delete_dialog))

            Text(
                text = stringResource(id = R.string.manage_food_items_merge_delete_selection_label),
                modifier = Modifier.padding(top = 4.dp)
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = state.chosenItem?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                )

                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
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
}

@Preview
@Composable
fun ManageFoodItemsPagePreview(@PreviewParameter(ItemPreviewParameterProvider::class) foodItems: List<FoodItem>) {
    SymptomTrackerTheme {
        ManageFoodItemsPage(
            navigateBack = {},
            foodItemsState = FoodItemsUiState.Data(foodItems),
            userActionState = null,
            onActionChosen = { _, _ -> },
            onEditNameChange = { },
            onMergeCandidateChosen = { },
            onSubmitAction = { },
            onCancelAction = { }
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
            onClose = {}
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
            onClose = { }
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
            onClose = { }
        )
    }
}
