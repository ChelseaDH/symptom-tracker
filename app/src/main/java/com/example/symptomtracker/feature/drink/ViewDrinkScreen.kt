package com.example.symptomtracker.feature.drink

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
import com.example.symptomtracker.core.domain.model.DrinkLog
import com.example.symptomtracker.core.ui.DrinkLogPreviewParameterProvider
import com.example.symptomtracker.core.ui.ViewLogScreen
import com.example.symptomtracker.core.ui.ViewLogUiState

@Composable
fun ViewDrinkRoute(
    navigateBack: () -> Unit,
    navigateToEdit: () -> Unit,
    navigateToCopy: (DrinkLog) -> Unit,
    viewModel: ViewDrinkViewModel = hiltViewModel(),
) {
    val uiState: ViewDrinkUiState by viewModel.uiState.collectAsState()
    val navEvent by viewModel.navigationEvent.collectAsState()

    ViewDrinkScreen(
        navigateBack = navigateBack,
        state = uiState,
        eventSink = viewModel::handleEvent,
    )

    LaunchedEffect(navEvent) {
        when (val event = navEvent) {
            is NavigationEvent.NavigateToEdit -> {
                navigateToEdit()
                viewModel.handleEvent(ViewDrinkEvent.NavigationHandled)
            }

            is NavigationEvent.NavigateToCopy -> {
                navigateToCopy(event.drinkLog)
                viewModel.handleEvent(ViewDrinkEvent.NavigationHandled)
            }

            null -> {}
        }
    }
}

@Composable
fun ViewDrinkScreen(
    navigateBack: () -> Unit,
    state: ViewDrinkUiState = ViewLogUiState.Loading,
    eventSink: (ViewDrinkEvent) -> Unit,
) {
    ViewLogScreen(
        navigateBack = navigateBack,
        uiState = state,
        title = R.string.add_drink_text,
        deleteLog = { eventSink(ViewDrinkEvent.DeleteLog(it)) },
        onEdit = { eventSink(ViewDrinkEvent.EditLog) },
        onCopy = { eventSink(ViewDrinkEvent.CopyLog) },
        bodyContent = {
            it.items.forEach { item ->
                ListItem(headlineContent = { Text(text = item.name) })
            }
        })
}

@Preview(showBackground = true)
@Composable
fun ViewDrinkContentPreview(
    @PreviewParameter(
        DrinkLogPreviewParameterProvider::class, limit = 1
    ) drinkLog: DrinkLog
) {
    ViewDrinkScreen(
        navigateBack = {},
        state = ViewLogUiState.Data(drinkLog),
        eventSink = {},
    )
}
