package com.example.symptomtracker.feature.food

import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.symptomtracker.R
import com.example.symptomtracker.core.model.FoodLog
import com.example.symptomtracker.core.ui.FoodLogPreviewParameterProvider
import com.example.symptomtracker.core.ui.ViewLogScreen
import com.example.symptomtracker.core.ui.ViewLogUiState

@Composable
fun ViewFoodRoute(
    navigateBack: () -> Unit,
    navigateToEdit: () -> Unit,
    viewModel: ViewFoodViewModel = hiltViewModel(),
) {
    ViewFoodScreen(
        navigateBack = navigateBack,
        state = viewModel.uiState,
        onDelete = viewModel::deleteLog,
        onEdit = navigateToEdit,
    )
}

@Composable
fun ViewFoodScreen(
    navigateBack: () -> Unit,
    state: ViewFoodUiState = ViewLogUiState.Loading,
    onDelete: (FoodLog) -> Unit,
    onEdit: () -> Unit,
) {
    ViewLogScreen(navigateBack = navigateBack,
        uiState = state,
        title = R.string.add_food_text,
        deleteLog = onDelete,
        onEdit = onEdit,
        bodyContent = {
            it.items.forEach { item ->
                ListItem(headlineContent = { Text(text = item.name) })
            }
        })
}

@Preview(showBackground = true)
@Composable
fun ViewFoodContentPreview(
    @PreviewParameter(
        FoodLogPreviewParameterProvider::class, limit = 1
    ) foodLog: FoodLog
) {
    ViewFoodScreen(
        navigateBack = {},
        state = ViewLogUiState.Data(foodLog),
        onDelete = {},
        onEdit = {},
    )
}
