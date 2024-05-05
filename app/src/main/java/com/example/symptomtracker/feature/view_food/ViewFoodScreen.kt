package com.example.symptomtracker.feature.view_food

import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.symptomtracker.R
import com.example.symptomtracker.core.database.model.FoodLogWithItems
import com.example.symptomtracker.core.ui.FoodLogPreviewParameterProvider
import com.example.symptomtracker.core.ui.ViewLogScreen
import com.example.symptomtracker.core.ui.ViewLogUiState

@Composable
fun ViewFoodRoute(
    navigateBack: () -> Unit,
    viewModel: ViewFoodViewModel = hiltViewModel()
) {
    ViewFoodScreen(navigateBack = navigateBack, state = viewModel.uiState)
}

@Composable
fun ViewFoodScreen(
    navigateBack: () -> Unit,
    state: ViewFoodUiState = ViewLogUiState.Loading,
) {
    ViewLogScreen(
        navigateBack = navigateBack,
        uiState = state,
        title = R.string.add_food_text,
        bodyContent = {
            it.items.forEach { item ->
                ListItem(
                    headlineContent = { Text(text = item.name) }
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ViewFoodContentPreview(
    @PreviewParameter(
        FoodLogPreviewParameterProvider::class,
        limit = 1
    ) foodLog: FoodLogWithItems
) {
    ViewFoodScreen(
        navigateBack = {},
        state = ViewLogUiState.Data(foodLog),
    )
}
