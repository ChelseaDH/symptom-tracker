package com.example.symptomtracker.feature.symptom

import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.symptomtracker.R
import com.example.symptomtracker.core.domain.model.SymptomLog
import com.example.symptomtracker.core.domain.model.getDisplayString
import com.example.symptomtracker.core.ui.SymptomLogPreviewParameterProvider
import com.example.symptomtracker.core.ui.ViewLogScreen
import com.example.symptomtracker.core.ui.ViewLogUiState

@Composable
fun ViewSymptomRoute(
    navigateBack: () -> Unit,
    navigateToEdit: () -> Unit,
    viewModel: ViewSymptomViewModel = hiltViewModel()
) {
    val uiState: ViewSymptomUiState by viewModel.uiState.collectAsState()

    ViewSymptomScreen(
        navigateBack = navigateBack,
        eventSink = viewModel::handleEvent,
        onEdit = navigateToEdit,
        state = uiState,
    )
}

@Composable
internal fun ViewSymptomScreen(
    navigateBack: () -> Unit,
    eventSink: (ViewSymptomEvent) -> Unit,
    onEdit: () -> Unit,
    state: ViewSymptomUiState = ViewLogUiState.Loading,
) {
    ViewLogScreen(
        navigateBack = navigateBack,
        uiState = state,
        title = R.string.add_symptom_text,
        deleteLog = { eventSink(ViewSymptomEvent.DeleteLog(it)) },
        onEdit = onEdit,
        bodyContent = {
            it.items.forEach { item ->
                ListItem(
                    headlineContent = { Text(text = item.getDisplayString()) }
                )
            }
        }
    )
}

@Preview
@Composable
fun ViewSymptomScreenPreview(
    @PreviewParameter(
        SymptomLogPreviewParameterProvider::class,
        limit = 1
    ) symptomLog: SymptomLog
) {
    ViewSymptomScreen(
        navigateBack = {},
        eventSink = {},
        onEdit = {},
        state = ViewLogUiState.Data(symptomLog),
    )
}
