import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.symptomtracker.R
import com.example.symptomtracker.data.food.Item
import com.example.symptomtracker.ui.AppViewModelProvider
import com.example.symptomtracker.ui.food.FoodEntryViewModel
import com.example.symptomtracker.ui.food.FoodLogUiState
import com.example.symptomtracker.ui.theme.SymptomTrackerTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FoodEntryViewModel = viewModel(factory = AppViewModelProvider.Factory),
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
            foodLogUiState = viewModel.foodLogUiState,
            chosenItems = viewModel.chosenItems,
            onChosenItemUpdated = viewModel::updateChosenItem,
            onNameUpdated = viewModel::updateItemName,
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
    chosenItems: List<Item>,
    onChosenItemUpdated: (Item) -> Unit,
    onNameUpdated: (String) -> Unit,
    onAddItem: () -> Unit,
    onCreateItem: () -> Unit,
    onDeleteItem: (Item) -> Unit,
    onClearChosenItem: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier
        .fillMaxWidth()
        .padding(all = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FoodLogItemInput(
            availableItems = foodLogUiState.availableItems,
            itemName = foodLogUiState.itemName,
            chosenItem = foodLogUiState.chosenItem,
            onChosenItemUpdated = onChosenItemUpdated,
            onNameUpdated = onNameUpdated,
            onAddItem = onAddItem,
            onCreateItem = onCreateItem,
            onClearChosenItem = onClearChosenItem,
            canCreateNewItem = foodLogUiState.canCreateNewItemFromInput
        )
        Divider()
        FoodLogItemList(itemList = chosenItems, onDeleteItem = onDeleteItem)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodLogItemList(
    itemList: List<Item>,
    onDeleteItem: (Item) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        items(items = itemList, key = { it.itemId }) { item ->
            ListItem(
                headlineText = { Text(text = item.name) },
                trailingContent = {
                    IconButton(onClick = { onDeleteItem(item) }) {
                        Icon(imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(
                                R.string.delete_item_cd))
                    }
                },
                modifier = modifier
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodLogItemInput(
    availableItems: List<Item>,
    itemName: String,
    chosenItem: Item?,
    canCreateNewItem: Boolean,
    onChosenItemUpdated: (Item) -> Unit,
    onNameUpdated: (String) -> Unit,
    onClearChosenItem: () -> Unit,
    onAddItem: () -> Unit,
    onCreateItem: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectorExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ExposedDropdownMenuBox(
            expanded = selectorExpanded,
            onExpandedChange = { selectorExpanded = !selectorExpanded }
        ) {
            OutlinedTextField(
                value = chosenItem?.name ?: itemName,
                onValueChange = {
                    onNameUpdated(it)
                    selectorExpanded = true
                },
                label = {
                    Text(text = stringResource(id = R.string.add_food_text),
                        color = MaterialTheme.colorScheme.onPrimaryContainer)
                },
                modifier = Modifier.menuAnchor(),
                trailingIcon = {
                    IconButton(onClick = { onClearChosenItem() }) {
                        Icon(imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(id = R.string.clear_food_input_cd))
                    }
                },
                singleLine = true,
            )
            val filteredOptions =
                availableItems.filter { it.name.contains(itemName, ignoreCase = true) }
            ExposedDropdownMenu(
                expanded = selectorExpanded,
                onDismissRequest = { selectorExpanded = false }) {
                if (filteredOptions.isNotEmpty()) {
                    for (option in filteredOptions) {
                        DropdownMenuItem(text = { Text(text = option.name) },
                            onClick = {
                                onChosenItemUpdated(option)
                                selectorExpanded = false
                            })
                    }
                }
                if (filteredOptions.isNotEmpty() && canCreateNewItem) {
                    Divider()
                }
                if (canCreateNewItem) {
                    DropdownMenuItem(text = { Text(text = itemName) },
                        onClick = { onCreateItem() },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Create new item"
                            )
                        }
                    )
                }
            }
        }
        FloatingActionButton(onClick = { onAddItem() }) {
            Icon(imageVector = Icons.Default.Add,
                contentDescription = stringResource(id = R.string.add_food_cd))
        }
    }
}

@Composable
@Preview(showSystemUi = true)
fun AddFoodScreenPreview() {
    SymptomTrackerTheme {
        FoodEntryBody(
            foodLogUiState = FoodLogUiState(
                availableItems = listOf(
                    Item(itemId = 1, name = "Oats"),
                    Item(itemId = 2, name = "Banana"),
                    Item(itemId = 3, name = "Egg"),
                    Item(itemId = 4, name = "Oat milk")
                ),
                chosenItem = null
            ),
            chosenItems = listOf(
                Item(itemId = 1, name = "Oats"),
                Item(itemId = 2, name = "Banana"),
            ),
            onChosenItemUpdated = {},
            onNameUpdated = {},
            onAddItem = {},
            onCreateItem = {},
            onDeleteItem = {},
            onClearChosenItem = {}
        )
    }
}