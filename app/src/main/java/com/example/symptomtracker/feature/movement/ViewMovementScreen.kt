package com.example.symptomtracker.feature.movement

import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.symptomtracker.R
import com.example.symptomtracker.core.model.MovementLog
import com.example.symptomtracker.core.model.getDisplayName
import com.example.symptomtracker.core.ui.MovementLogPreviewParameterProvider
import com.example.symptomtracker.core.ui.ViewLogScreen
import com.example.symptomtracker.core.ui.ViewLogUiState

@Composable
fun ViewMovementRoute(
    navigateBack: () -> Unit,
    navigateToEdit: () -> Unit,
    viewModel: ViewMovementViewModel = hiltViewModel()
) {
    val uiState: ViewMovementUiState by viewModel.uiState.collectAsState()

    ViewMovementScreen(
        navigateBack = navigateBack,
        deleteLog = viewModel::deleteLog,
        onEdit = navigateToEdit,
        state = uiState,
    )
}

@Composable
fun ViewMovementScreen(
    navigateBack: () -> Unit,
    deleteLog: (MovementLog) -> Unit,
    onEdit: () -> Unit,
    state: ViewMovementUiState = ViewLogUiState.Loading,
) {
    ViewLogScreen(
        navigateBack = navigateBack,
        uiState = state,
        title = R.string.add_movement_text,
        deleteLog = deleteLog,
        onEdit = onEdit,
        bodyContent = {
            ListItem(
                headlineContent = { Text(text = it.stoolType.getDisplayName()) }
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ViewMovementContentPreview(
    @PreviewParameter(
        MovementLogPreviewParameterProvider::class,
        limit = 1
    ) log: MovementLog
) {
    ViewMovementScreen(
        navigateBack = {},
        deleteLog = {},
        onEdit = {},
        state = ViewLogUiState.Data(log),
    )
}
