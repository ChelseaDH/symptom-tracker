package com.example.symptomtracker.feature.food

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.symptomtracker.R
import com.example.symptomtracker.core.model.FoodItem
import com.example.symptomtracker.core.ui.SymptomTrackerTheme
import com.example.symptomtracker.ui.SymptomTrackerTopAppBar

@Composable
fun ManageFoodItemsRoute(
    navigateBack: () -> Unit,
    viewModel: ManageItemsViewModel = hiltViewModel(),
) {
    val uiState: ManageItemsUiState by viewModel.uiState.collectAsState()

    ManageFoodItemsPage(navigateBack, uiState, viewModel::editItemName)
}

@Composable
internal fun ManageFoodItemsPage(
    navigateBack: () -> Unit,
    uiState: ManageItemsUiState,
    onEdit: (FoodItem, String) -> Unit,
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
            when (uiState) {
                is ManageItemsUiState.Loading -> {}
                is ManageItemsUiState.Data -> Column {
                    if (uiState.items.isEmpty()) {
                        Text(text = stringResource(id = R.string.no_items_found))
                    }

                    uiState.items.forEach { item ->
                        FoodItemRow(foodItem = item, onEdit = onEdit)
                    }
                }
            }
        }
    }
}

@Composable
fun FoodItemRow(foodItem: FoodItem, onEdit: (FoodItem, String) -> Unit) {
    var editing by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf(foodItem.name) }

    if (editing) {
        ListItem(
            headlineContent = { TextField(value = itemName, onValueChange = { itemName = it }) },
            trailingContent = {
                Row {
                    IconButton(onClick = {
                        onEdit(foodItem, itemName)
                        editing = false
                    }) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(
                                id = R.string.manage_food_items_save_cd,
                                formatArgs = arrayOf(foodItem.name)
                            )
                        )
                    }
                    IconButton(onClick = { editing = false }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(
                                id = R.string.manage_food_items_cancel_edit_cd,
                                formatArgs = arrayOf(foodItem.name)
                            )
                        )
                    }
                }
            }
        )
    } else {
        ListItem(
            headlineContent = { Text(text = foodItem.name) },
            trailingContent = {
                IconButton(onClick = { editing = true }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(
                            id = R.string.manage_food_items_edit_cd,
                            formatArgs = arrayOf(foodItem.name)
                        )
                    )
                }
            }
        )
    }
}

@Preview
@Composable
fun ManageFoodItemsPagePreview() {
    SymptomTrackerTheme {
        ManageFoodItemsPage(
            navigateBack = {},
            uiState = ManageItemsUiState.Data(listOf(FoodItem(id = 1, name = "Oats"))),
            onEdit = { _, _ -> }
        )
    }
}
