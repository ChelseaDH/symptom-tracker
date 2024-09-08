import androidx.annotation.StringRes
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
import com.example.symptomtracker.R
import com.example.symptomtracker.core.designsystem.SymptomTrackerTheme
import com.example.symptomtracker.core.designsystem.component.DateTimeInput
import com.example.symptomtracker.core.designsystem.component.DateTimeInputRow
import com.example.symptomtracker.core.designsystem.component.OutlinedInputTextFieldWithDropdown
import com.example.symptomtracker.core.domain.model.FoodItem
import com.example.symptomtracker.core.ui.ItemPreviewParameterProvider
import com.example.symptomtracker.feature.food.AbstractFoodEntryViewModel
import com.example.symptomtracker.feature.food.FoodEntryUiState
import com.example.symptomtracker.feature.food.SearchState
import com.example.symptomtracker.ui.SymptomTrackerTopAppBar
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

@Composable
internal fun FoodEntryScreen(
    navigateBack: () -> Unit,
    @StringRes titleId: Int,
    modifier: Modifier = Modifier,
    viewModel: AbstractFoodEntryViewModel,
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            SymptomTrackerTopAppBar(title = stringResource(titleId),
                canNavigateBack = true,
                navigateUp = navigateBack,
                actions = {
                    TextButton(onClick = {
                        coroutineScope.launch {
                            viewModel.submit()
                            navigateBack()
                        }
                    }) {
                        Text(text = stringResource(R.string.action_save))
                    }
                })
        }, modifier = modifier
    ) { innerPadding ->
        FoodEntryBody(
            uiState = viewModel.uiState,
            onChosenItemUpdated = viewModel::updateSelectedSearchItem,
            onNameUpdated = viewModel::updateSearchInput,
            onDateChanged = viewModel::updateDate,
            onTimeChanged = viewModel::updateTime,
            onAddItem = viewModel::addItem,
            onCreateItem = {
                coroutineScope.launch {
                    viewModel.createNewItemFromInput()
                }
            },
            onDeleteItem = viewModel::removeItem,
            onClearChosenItem = viewModel::clearSearch,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun FoodEntryBody(
    uiState: FoodEntryUiState,
    onChosenItemUpdated: (FoodItem) -> Unit,
    onNameUpdated: (String) -> Unit,
    onDateChanged: (LocalDate) -> Unit,
    onTimeChanged: (LocalTime) -> Unit,
    onAddItem: () -> Unit,
    onCreateItem: () -> Unit,
    onDeleteItem: (FoodItem) -> Unit,
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
            searchState = uiState.searchState,
            dateTimeInput = uiState.dateTimeInput,
            onChosenItemUpdated = onChosenItemUpdated,
            onNameUpdated = onNameUpdated,
            onDateChanged = onDateChanged,
            onTimeChanged = onTimeChanged,
            onAddItem = onAddItem,
            onCreateItem = onCreateItem,
            onClearChosenItem = onClearChosenItem,
        )
        HorizontalDivider()
        FoodLogItemList(
            foodItemEntityList = uiState.selectedFoodItems, onDeleteItem = onDeleteItem
        )
    }
}

@Composable
fun FoodLogItemList(
    foodItemEntityList: List<FoodItem>,
    onDeleteItem: (FoodItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        items(items = foodItemEntityList, key = { it.id }) { item ->
            ListItem(headlineContent = { Text(text = item.name) }, trailingContent = {
                IconButton(onClick = { onDeleteItem(item) }) {
                    Icon(
                        imageVector = Icons.Default.Delete, contentDescription = stringResource(
                            R.string.delete_item_cd
                        )
                    )
                }
            }, modifier = modifier
            )
        }
    }
}

@Composable
fun FoodLogItemInput(
    searchState: SearchState,
    dateTimeInput: DateTimeInput,
    onChosenItemUpdated: (FoodItem) -> Unit,
    onDateChanged: (LocalDate) -> Unit,
    onTimeChanged: (LocalTime) -> Unit,
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
            availableOptions = searchState.results,
            getOptionDisplayName = { it.name },
            textValue = searchState.input,
            onTextValueUpdated = onNameUpdated,
            canCreateOption = searchState.canCreateNewItem,
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
fun AddFoodScreenPreview(@PreviewParameter(ItemPreviewParameterProvider::class) foodItems: List<FoodItem>) {
    SymptomTrackerTheme {
        FoodEntryBody(
            uiState = FoodEntryUiState().copy(
                searchState = SearchState(results = foodItems)
            ),
            onChosenItemUpdated = {},
            onNameUpdated = {},
            onAddItem = {},
            onDateChanged = {},
            onTimeChanged = {},
            onCreateItem = {},
            onDeleteItem = {},
            onClearChosenItem = {},
        )
    }
}
