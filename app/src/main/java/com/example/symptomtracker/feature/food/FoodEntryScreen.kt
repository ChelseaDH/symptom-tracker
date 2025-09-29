package com.example.symptomtracker.feature.food

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.symptomtracker.core.domain.model.FoodItem
import com.example.symptomtracker.core.ui.ItemPreviewParameterProvider
import com.example.symptomtracker.ui.SymptomTrackerTopAppBar

@Composable
internal fun FoodEntryScreen(
    navigateBack: () -> Unit,
    @StringRes titleId: Int,
    modifier: Modifier = Modifier,
    viewModel: AbstractFoodEntryViewModel,
) {
    Scaffold(
        topBar = {
            SymptomTrackerTopAppBar(
                title = stringResource(titleId),
                canNavigateBack = true,
                navigateUp = navigateBack,
                actions = {
                    TextButton(onClick = {
                        viewModel.submit()
                        navigateBack()
                    }) {
                        Text(text = stringResource(R.string.action_save))
                    }
                })
        }, modifier = modifier
    ) { innerPadding ->
        FoodEntryBody(
            uiState = viewModel.uiState,
            eventSink = viewModel::handleEvent,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun FoodEntryBody(
    uiState: FoodEntryUiState,
    eventSink: (FoodEntryEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(all = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FoodLogItemInput(
            searchState = uiState.searchState,
            dateTimeInput = uiState.dateTimeInput,
            eventSink = eventSink,
        )
        HorizontalDivider()
        FoodLogItemList(
            foodItemEntityList = uiState.selectedFoodItems,
            onDeleteItem = { eventSink(FoodEntryEvent.RemoveItem(it)) }
        )
    }
}

@Composable
internal fun FoodLogItemList(
    foodItemEntityList: List<FoodItem>,
    onDeleteItem: (FoodItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        items(items = foodItemEntityList, key = { it.id }) { item ->
            ListItem(
                headlineContent = { Text(text = item.name) }, trailingContent = {
                    IconButton(onClick = { onDeleteItem(item) }) {
                        DeleteIcon(contentDescription = stringResource(R.string.delete_item_cd))
                    }
                }, modifier = modifier
            )
        }
    }
}

@Composable
internal fun FoodLogItemInput(
    searchState: SearchState,
    dateTimeInput: DateTimeInput,
    eventSink: (FoodEntryEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        DateTimeInputRow(
            dateTimeInput = dateTimeInput,
            onDateChanged = { eventSink(FoodEntryEvent.UpdateDate(it)) },
            onTimeChanged = { eventSink(FoodEntryEvent.UpdateTime(it)) },
        )

        LabelledOutlinedTextInputFieldWithDropdown(
            label = stringResource(id = R.string.add_food_text),
            value = searchState.input,
            availableOptions = searchState.results,
            canCreateOption = searchState.canCreateNewItem,
            onValueChange = { eventSink(FoodEntryEvent.UpdateSearchInput(it)) },
            getOptionDisplayName = { it.name },
            onCreateOption = { eventSink(FoodEntryEvent.CreateNewItemFromInput) },
            onClearInput = { eventSink(FoodEntryEvent.ClearSearch) },
            onChosenOptionUpdated = { eventSink(FoodEntryEvent.UpdateSelectedSearchItem(it)) },
            modifier = Modifier.fillMaxWidth()
        )

        FilledTonalButtonWithIcon(
            textId = R.string.action_add_to_log,
            icon = {
                AddIcon(contentDescription = stringResource(id = R.string.add_food_cd))
            },
            onClick = { eventSink(FoodEntryEvent.AddItem) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
@Preview(showSystemUi = true)
fun AddFoodScreenPreview(@PreviewParameter(ItemPreviewParameterProvider::class) foodItems: List<FoodItem>) {
    SymptomTrackerTheme {
        FoodEntryBody(
            uiState = FoodEntryUiState().copy(
                searchState = SearchState(results = foodItems)
            ),
            eventSink = {},
        )
    }
}
