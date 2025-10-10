package com.example.symptomtracker.feature.drink

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.symptomtracker.core.domain.model.DrinkItem
import com.example.symptomtracker.core.ui.DrinkItemPreviewParameterProvider
import com.example.symptomtracker.ui.SymptomTrackerTopAppBar

@Composable
fun ManageDrinkItemsRoute(
    navigateBack: () -> Unit,
    viewModel: ManageDrinkItemsViewModel = hiltViewModel(),
) {
    val drinkItemsState by viewModel.drinkItemsState.collectAsState()
    val userActionState by viewModel.userActionState.collectAsState()

    ManageDrinkItemsPage(
        navigateBack = navigateBack,
        drinkItemsState = drinkItemsState,
        userActionState = userActionState,
        eventSink = viewModel::handleEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ManageDrinkItemsPage(
    navigateBack: () -> Unit,
    drinkItemsState: DrinkItemsUiState,
    userActionState: DrinkActionState?,
    eventSink: (ManageDrinkEvent) -> Unit,
) {
    Scaffold(
        topBar = {
            SymptomTrackerTopAppBar(
                title = stringResource(id = R.string.manage_drink_items_title),
                navigateUp = navigateBack,
            )
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            when (drinkItemsState) {
                is DrinkItemsUiState.Loading -> {}
                is DrinkItemsUiState.Data -> Column {
                    if (drinkItemsState.items.isEmpty()) {
                        Text(text = stringResource(id = R.string.no_items_found))
                    }

                    LazyColumn {
                        items(items = drinkItemsState.items) { item ->
                            DrinkItemRow(
                                drinkItem = item,
                                onActionChosen = { drinkItem, action ->
                                    eventSink(
                                        ManageDrinkEvent.StartAction(
                                            drinkItem = drinkItem,
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
                is DrinkActionState.Edit ->
                    EditDialog(
                        state = userActionState,
                        onEditNameChange = { eventSink(ManageDrinkEvent.UpdateName(it)) },
                        onSubmit = { eventSink(ManageDrinkEvent.SubmitAction) },
                        onClose = { eventSink(ManageDrinkEvent.CancelAction) },
                    )

                is DrinkActionState.Delete.Direct ->
                    DirectDeleteDialog(
                        state = userActionState,
                        onSubmit = { eventSink(ManageDrinkEvent.SubmitAction) },
                        onClose = { eventSink(ManageDrinkEvent.CancelAction) },
                    )

                is DrinkActionState.Delete.Merge ->
                    MergeDeleteDialog(
                        state = userActionState,
                        onSelectedItemUpdated = { eventSink(ManageDrinkEvent.ChooseMergeCandidate(it)) },
                        onSubmit = { eventSink(ManageDrinkEvent.SubmitAction) },
                        onClose = { eventSink(ManageDrinkEvent.CancelAction) },
                    )

                null -> {}
            }
        }
    }
}

@Composable
fun DrinkItemRow(
    drinkItem: DrinkItem,
    onActionChosen: (DrinkItem, DrinkItemAction) -> Unit,
) {
    var actionMenuOpen by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = { Text(text = drinkItem.name) },
        trailingContent = {
            IconButton(onClick = { actionMenuOpen = true }) {
                Icon(
                    painter = painterResource(R.drawable.outline_more_vert_24),
                    contentDescription = stringResource(
                        id = R.string.manage_items_options_menu_cd,
                        formatArgs = arrayOf(drinkItem.name),
                    )
                )
            }
            DropdownMenu(expanded = actionMenuOpen, onDismissRequest = { actionMenuOpen = false }) {
                DropdownMenuItem(
                    text = { Text(text = stringResource(id = R.string.action_edit)) },
                    onClick = {
                        onActionChosen(drinkItem, DrinkItemAction.EDIT)
                        actionMenuOpen = false
                    },
                    leadingIcon = {
                        EditIcon(
                            contentDescription = stringResource(
                                id = R.string.manage_items_edit,
                                formatArgs = arrayOf(drinkItem.name),
                            )
                        )
                    }
                )
                DropdownMenuItem(
                    text = { Text(text = stringResource(id = R.string.action_delete)) },
                    onClick = {
                        onActionChosen(drinkItem, DrinkItemAction.DELETE)
                        actionMenuOpen = false
                    },
                    leadingIcon = {
                        DeleteIcon(
                            contentDescription = stringResource(
                                id = R.string.manage_items_delete,
                                formatArgs = arrayOf(drinkItem.name),
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
    state: DrinkActionState.Edit,
    onEditNameChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onClose: () -> Unit,
) {
    Dialog(
        title = stringResource(R.string.manage_items_edit).format(state.drinkItem.name),
        confirmButtonText = R.string.action_save,
        confirmButtonEnabled = state.canSubmit,
        icon = {
            EditIcon(
                contentDescription = stringResource(
                    id = R.string.manage_items_edit,
                    formatArgs = arrayOf(state.drinkItem.name),
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
    state: DrinkActionState.Delete.Direct,
    onSubmit: () -> Unit,
    onClose: () -> Unit,
) {
    Dialog(
        title = stringResource(R.string.manage_items_delete).format(state.drinkItem.name),
        confirmButtonText = R.string.action_delete,
        icon = {
            DeleteIcon(
                contentDescription = stringResource(
                    id = R.string.manage_items_delete,
                    formatArgs = arrayOf(state.drinkItem.name),
                )
            )
        },
        onSubmit = onSubmit,
        onClose = onClose,
    ) {
        Text(text = stringResource(id = R.string.manage_items_direct_delete_dialog))
    }
}

@Composable
internal fun MergeDeleteDialog(
    state: DrinkActionState.Delete.Merge,
    onSelectedItemUpdated: (DrinkItem) -> Unit,
    onSubmit: () -> Unit,
    onClose: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Dialog(
        title = stringResource(R.string.manage_items_merge).format(state.drinkItem.name),
        confirmButtonText = R.string.action_merge,
        confirmButtonEnabled = state.canSubmit,
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.outline_merge_24),
                contentDescription = stringResource(
                    id = R.string.manage_items_merge,
                    formatArgs = arrayOf(state.drinkItem.name),
                )
            )
        },
        onSubmit = onSubmit,
        onClose = onClose
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(text = stringResource(id = R.string.manage_drink_items_merge_delete_dialog))

            LabelledOutlinedReadOnlyDropdown(
                label = stringResource(id = R.string.manage_items_merge_delete_selection_label),
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
fun ManageDrinkItemsPagePreview(@PreviewParameter(DrinkItemPreviewParameterProvider::class) drinkItems: List<DrinkItem>) {
    SymptomTrackerTheme {
        ManageDrinkItemsPage(
            navigateBack = {},
            drinkItemsState = DrinkItemsUiState.Data(drinkItems),
            userActionState = null,
            eventSink = {},
        )
    }
}

@Preview
@Composable
fun EditDialogPreview() {
    SymptomTrackerTheme {
        EditDialog(
            state = DrinkActionState.Edit(drinkItem = DrinkItem(id = 1, name = "Water")),
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
            state = DrinkActionState.Delete.Direct(drinkItem = DrinkItem(id = 1, name = "Water")),
            onSubmit = { },
            onClose = { },
        )
    }
}

@Preview
@Composable
fun MergeDeleteDialogPreview() {
    SymptomTrackerTheme {
        MergeDeleteDialog(
            state = DrinkActionState.Delete.Merge(
                drinkItem = DrinkItem(id = 1, name = "Water"),
                mergeCandidates = listOf(
                    DrinkItem(id = 2, name = "Coffee"),
                    DrinkItem(id = 3, name = "Tea"),
                ),
            ),
            onSelectedItemUpdated = { },
            onSubmit = { },
            onClose = { },
        )
    }
}
