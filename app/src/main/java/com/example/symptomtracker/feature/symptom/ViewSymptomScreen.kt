package com.example.symptomtracker.feature.symptom

import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.symptomtracker.R
import com.example.symptomtracker.core.model.SymptomLogWithSymptoms
import com.example.symptomtracker.core.model.getDisplayString
import com.example.symptomtracker.core.ui.SymptomLogPreviewParameterProvider
import com.example.symptomtracker.core.ui.ViewLogScreen
import com.example.symptomtracker.core.ui.ViewLogUiState

@Composable
fun ViewSymptomRoute(
    navigateBack: () -> Unit,
    navigateToEdit: () -> Unit,
    viewModel: ViewSymptomViewModel = hiltViewModel()
) {
    ViewSymptomScreen(
        navigateBack = navigateBack,
        onDelete = viewModel::deleteLog,
        onEdit = navigateToEdit,
        state = viewModel.uiState,
    )
}

@Composable
internal fun ViewSymptomScreen(
    navigateBack: () -> Unit,
    onDelete: (SymptomLogWithSymptoms) -> Unit,
    onEdit: () -> Unit,
    state: ViewSymptomUiState = ViewLogUiState.Loading,
) {
    ViewLogScreen(
        navigateBack = navigateBack,
        uiState = state,
        title = R.string.add_symptom_text,
        deleteLog = onDelete,
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
    ) symptomLog: SymptomLogWithSymptoms
) {
    ViewSymptomScreen(
        navigateBack = {},
        onDelete = {},
        onEdit = {},
        state = ViewLogUiState.Data(symptomLog),
    )
}
