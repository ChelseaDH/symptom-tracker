import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.symptomtracker.R
import com.example.symptomtracker.core.database.model.Item
import com.example.symptomtracker.core.ui.DateInputFields
import com.example.symptomtracker.core.ui.DateTimeInput
import com.example.symptomtracker.core.ui.DateTimeInputRow
import com.example.symptomtracker.core.ui.ItemPreviewParameterProvider
import com.example.symptomtracker.core.ui.OutlinedInputTextFieldWithDropdown
import com.example.symptomtracker.core.ui.SymptomTrackerTheme
import com.example.symptomtracker.core.ui.TimeInputFields
import com.example.symptomtracker.feature.food_entry.FoodEntryViewModel
import com.example.symptomtracker.feature.food_entry.FoodLogUiState
import com.example.symptomtracker.ui.SymptomTrackerTopAppBar
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun FoodEntryScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FoodEntryViewModel = hiltViewModel(),
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            SymptomTrackerTopAppBar(
                title = stringResource(R.string.log_food_title),
                canNavigateBack = true,
                navigateUp = navigateBack,
                actions = {
                    TextButton(onClick = {
                        coroutineScope.launch {
                            viewModel.saveFoodLog()
                            navigateBack()
                        }
                    }) {
                        Text(text = stringResource(R.string.save_button_text))
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        FoodEntryBody(
            foodLogUiState = viewModel.uiState,
            onChosenItemUpdated = viewModel::updateChosenItem,
            onNameUpdated = viewModel::updateItemName,
            onDateChanged = viewModel::updateDate,
            onTimeChanged = viewModel::updateTime,
            onAddItem = viewModel::addItem,
            onCreateItem = {
                coroutineScope.launch {
                    viewModel.insertNewItemFromInput()
                }
            },
            onDeleteItem = viewModel::removeItem,
            onClearChosenItem = viewModel::clearItemInputs,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun FoodEntryBody(
    foodLogUiState: FoodLogUiState,
    onChosenItemUpdated: (Item) -> Unit,
    onNameUpdated: (String) -> Unit,
    onDateChanged: (DateInputFields) -> Unit,
    onTimeChanged: (TimeInputFields) -> Unit,
    onAddItem: () -> Unit,
    onCreateItem: () -> Unit,
    onDeleteItem: (Item) -> Unit,
    onClearChosenItem: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(all = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FoodLogItemInput(
            availableItems = foodLogUiState.availableItems,
            itemName = foodLogUiState.itemName,
            dateTimeInput = foodLogUiState.dateTimeInput,
            onChosenItemUpdated = onChosenItemUpdated,
            onNameUpdated = onNameUpdated,
            onDateChanged = onDateChanged,
            onTimeChanged = onTimeChanged,
            onAddItem = onAddItem,
            onCreateItem = onCreateItem,
            onClearChosenItem = onClearChosenItem,
            canCreateNewItem = foodLogUiState.canCreateNewItemFromInput
        )
        HorizontalDivider()
        FoodLogItemList(itemList = foodLogUiState.foodLogDetails.items, onDeleteItem = onDeleteItem)
    }
}

@Composable
fun FoodLogItemList(
    itemList: List<Item>,
    onDeleteItem: (Item) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        items(items = itemList, key = { it.itemId }) { item ->
            ListItem(
                headlineContent = { Text(text = item.name) },
                trailingContent = {
                    IconButton(onClick = { onDeleteItem(item) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(
                                R.string.delete_item_cd
                            )
                        )
                    }
                },
                modifier = modifier
            )
        }
    }
}

@Composable
fun FoodLogItemInput(
    availableItems: List<Item>,
    itemName: String,
    dateTimeInput: DateTimeInput,
    canCreateNewItem: Boolean,
    onChosenItemUpdated: (Item) -> Unit,
    onDateChanged: (DateInputFields) -> Unit,
    onTimeChanged: (TimeInputFields) -> Unit,
    onNameUpdated: (String) -> Unit,
    onClearChosenItem: () -> Unit,
    onAddItem: () -> Unit,
    onCreateItem: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        DateTimeInputRow(
            dateTimeInput = dateTimeInput,
            onDateChanged = onDateChanged,
            onTimeChanged = onTimeChanged,
            labelOnTextField = true
        )
        OutlinedInputTextFieldWithDropdown(
            availableOptions = availableItems,
            getOptionDisplayName = { it.name },
            textValue = itemName,
            onTextValueUpdated = onNameUpdated,
            canCreateOption = canCreateNewItem,
            onCreateOption = onCreateItem,
            onClearInput = onClearChosenItem,
            onChosenOptionUpdated = onChosenItemUpdated,
            textLabelId = R.string.add_food_text,
            modifier = Modifier.fillMaxWidth()
        )
        FilledTonalButton(
            onClick = { onAddItem() },
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(id = R.string.add_food_cd),
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(text = "Add to log")
        }
    }
}

@Composable
@Preview(showSystemUi = true)
fun AddFoodScreenPreview(@PreviewParameter(ItemPreviewParameterProvider::class) items: List<Item>) {
    SymptomTrackerTheme {
        FoodEntryBody(
            foodLogUiState = FoodLogUiState(Calendar.getInstance()).copy(
                availableItems = items,
                chosenItem = null,
            ),
            onChosenItemUpdated = {},
            onNameUpdated = {},
            onAddItem = {},
            onDateChanged = {},
            onTimeChanged = {},
            onCreateItem = {},
            onDeleteItem = {},
            onClearChosenItem = {}
        )
    }
}
