package com.example.symptomtracker.feature.food

import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.symptomtracker.R
import com.example.symptomtracker.core.domain.model.FoodLog
import com.example.symptomtracker.core.ui.FoodLogPreviewParameterProvider
import com.example.symptomtracker.core.ui.ViewLogScreen
import com.example.symptomtracker.core.ui.ViewLogUiState

@Composable
fun ViewFoodRoute(
    navigateBack: () -> Unit,
    navigateToEdit: () -> Unit,
    navigateToCopy: (FoodLog) -> Unit,
    viewModel: ViewFoodViewModel = hiltViewModel(),
) {
    val uiState: ViewFoodUiState by viewModel.uiState.collectAsState()
    val navEvent by viewModel.navigationEvent.collectAsState()

    ViewFoodScreen(
        navigateBack = navigateBack,
        state = uiState,
        eventSink = viewModel::handleEvent,
    )

    LaunchedEffect(navEvent) {
        when (val event = navEvent) {
            is NavigationEvent.NavigateToEdit -> {
                navigateToEdit()
                viewModel.handleEvent(ViewFoodEvent.NavigationHandled)
            }

            is NavigationEvent.NavigateToCopy -> {
                navigateToCopy(event.foodLog)
                viewModel.handleEvent(ViewFoodEvent.NavigationHandled)
            }

            null -> {}
        }
    }
}

@Composable
fun ViewFoodScreen(
    navigateBack: () -> Unit,
    state: ViewFoodUiState = ViewLogUiState.Loading,
    eventSink: (ViewFoodEvent) -> Unit,
) {
    ViewLogScreen(navigateBack = navigateBack,
        uiState = state,
        title = R.string.add_food_text,
        deleteLog = { eventSink(ViewFoodEvent.DeleteLog(it)) },
        onEdit = { eventSink(ViewFoodEvent.EditLog) },
        onCopy = { eventSink(ViewFoodEvent.CopyLog) },
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
        eventSink = {},
    )
}
