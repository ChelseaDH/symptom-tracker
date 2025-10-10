package com.example.symptomtracker.feature.drink

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.example.symptomtracker.R
import com.example.symptomtracker.core.designsystem.SymptomTrackerTheme
import com.example.symptomtracker.core.designsystem.component.DateTimeInput
import com.example.symptomtracker.core.designsystem.component.DateTimeInputRow
import com.example.symptomtracker.core.designsystem.component.FilledTonalButtonWithIcon
import com.example.symptomtracker.core.designsystem.component.LabelledOutlinedTextInputFieldWithDropdown
import com.example.symptomtracker.core.designsystem.icon.AddIcon
import com.example.symptomtracker.core.designsystem.icon.DeleteIcon
import com.example.symptomtracker.core.domain.model.DrinkItem
import com.example.symptomtracker.core.ui.DrinkItemPreviewParameterProvider
import com.example.symptomtracker.ui.SymptomTrackerTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DrinkEntryScreen(
    navigateBack: () -> Unit,
    @StringRes titleId: Int,
    modifier: Modifier = Modifier,
    viewModel: AbstractDrinkEntryViewModel,
) {
    Scaffold(
        topBar = {
            SymptomTrackerTopAppBar(
                title = stringResource(titleId), navigateUp = navigateBack, actions = {
                    TextButton(onClick = {
                        viewModel.submit()
                        navigateBack()
                    }) {
                        Text(text = stringResource(R.string.action_save))
                    }
                })
        },
        modifier = modifier,
    ) { innerPadding ->
        DrinkEntryBody(
            uiState = viewModel.uiState,
            eventSink = viewModel::handleEvent,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
fun DrinkEntryBody(
    uiState: DrinkEntryUiState,
    eventSink: (DrinkEntryEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(all = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DrinkLogItemInput(
            searchState = uiState.searchState,
            dateTimeInput = uiState.dateTimeInput,
            eventSink = eventSink,
        )
        HorizontalDivider()
        DrinkLogItemList(
            drinkItemEntityList = uiState.selectedDrinkItems,
            onDeleteItem = { eventSink(DrinkEntryEvent.RemoveItem(it)) })
    }
}

@Composable
internal fun DrinkLogItemList(
    drinkItemEntityList: List<DrinkItem>,
    onDeleteItem: (DrinkItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        items(items = drinkItemEntityList, key = { it.id }) { item ->
            ListItem(
                headlineContent = { Text(text = item.name) },
                trailingContent = {
                    IconButton(onClick = { onDeleteItem(item) }) {
                        DeleteIcon(contentDescription = stringResource(R.string.delete_item_cd))
                    }
                },
                modifier = modifier,
            )
        }
    }
}

@Composable
internal fun DrinkLogItemInput(
    searchState: SearchState,
    dateTimeInput: DateTimeInput,
    eventSink: (DrinkEntryEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        DateTimeInputRow(
            dateTimeInput = dateTimeInput,
            onDateChanged = { eventSink(DrinkEntryEvent.UpdateDate(it)) },
            onTimeChanged = { eventSink(DrinkEntryEvent.UpdateTime(it)) },
        )

        LabelledOutlinedTextInputFieldWithDropdown(
            label = stringResource(id = R.string.add_drink_text),
            value = searchState.input,
            availableOptions = searchState.results,
            canCreateOption = searchState.canCreateNewItem,
            onValueChange = { eventSink(DrinkEntryEvent.UpdateSearchInput(it)) },
            getOptionDisplayName = { it.name },
            onCreateOption = { eventSink(DrinkEntryEvent.CreateNewItemFromInput) },
            onClearInput = { eventSink(DrinkEntryEvent.ClearSearch) },
            onChosenOptionUpdated = { eventSink(DrinkEntryEvent.UpdateSelectedSearchItem(it)) },
            modifier = Modifier.fillMaxWidth()
        )

        FilledTonalButtonWithIcon(
            textId = R.string.action_add_to_log,
            icon = {
                AddIcon(contentDescription = stringResource(id = R.string.add_drink_cd))
            },
            onClick = { eventSink(DrinkEntryEvent.AddItem) }, modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
@Preview(showSystemUi = true)
fun AddDrinkScreenPreview(@PreviewParameter(DrinkItemPreviewParameterProvider::class) drinkItems: List<DrinkItem>) {
    SymptomTrackerTheme {
        DrinkEntryBody(
            uiState = DrinkEntryUiState().copy(
                searchState = SearchState(results = drinkItems.map { DrinkItem(it.id, it.name) })
            ),
            eventSink = {},
        )
    }
}
