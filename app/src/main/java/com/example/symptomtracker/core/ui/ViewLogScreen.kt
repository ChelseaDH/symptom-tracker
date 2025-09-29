package com.example.symptomtracker.core.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.symptomtracker.R
import com.example.symptomtracker.core.designsystem.component.Dialog
import com.example.symptomtracker.core.designsystem.icon.DeleteIcon
import com.example.symptomtracker.core.designsystem.icon.EditIcon
import com.example.symptomtracker.core.domain.model.Log
import com.example.symptomtracker.ui.SymptomTrackerTopAppBar

@Composable
fun <L : Log> ViewLogScreen(
    navigateBack: () -> Unit,
    uiState: ViewLogUiState<L>,
    @StringRes title: Int,
    deleteLog: (L) -> Unit = {},
    onEdit: (() -> Unit)? = null,
    onCopy: (() -> Unit)? = null,
    bodyContent: @Composable ColumnScope.(L) -> Unit,
) {
    when (uiState) {
        is ViewLogUiState.Loading, ViewLogUiState.Empty -> ViewLogsScreen(navigateBack = navigateBack)

        is ViewLogUiState.Data -> {
            val openDeleteDialog = remember { mutableStateOf(false) }

            ViewLogsScreen(
                navigateBack = navigateBack,
                topBarActions = {
                    if (onEdit !== null) {
                        IconButton(onClick = onEdit) {
                            EditIcon(contentDescription = null)
                        }
                    }
                    if (onCopy !== null) {
                        IconButton(onClick = onCopy) {
                            Icon(
                                painter = painterResource(id = R.drawable.outline_content_copy_24),
                                contentDescription = null,
                            )
                        }
                    }
                    IconButton(onClick = { openDeleteDialog.value = true }) {
                        DeleteIcon(contentDescription = null)
                    }
                }
            ) {
                ViewLogBody(
                    log = uiState.log,
                    title = title,
                    bodyContent = bodyContent,
                )
            }

            if (openDeleteDialog.value) {
                DeleteLogAlertDialog(
                    onDismissRequest = { openDeleteDialog.value = false },
                    onDelete = {
                        deleteLog(uiState.log)
                        navigateBack()
                    }
                )
            }
        }
    }
}

@Composable
internal fun ViewLogsScreen(
    navigateBack: () -> Unit,
    topBarActions: @Composable (RowScope.() -> Unit) = {},
    content: @Composable ColumnScope.() -> Unit = {},
) {
    Scaffold(
        topBar = {
            SymptomTrackerTopAppBar(
                title = "",
                canNavigateBack = true,
                navigateUp = navigateBack,
                actions = topBarActions,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp)
        ) {
            content()
        }
    }
}

@Composable
internal fun <L : Log> ViewLogBody(
    modifier: Modifier = Modifier,
    log: L,
    @StringRes title: Int,
    bodyContent: @Composable ColumnScope.(L) -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(title),
                style = MaterialTheme.typography.headlineMedium,
            )
            LogDateTime(log = log)
        }
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            bodyContent(log)
        }
    }
}

@Composable
internal fun DeleteLogAlertDialog(
    onDismissRequest: () -> Unit,
    onDelete: () -> Unit,
) {
    Dialog(
        title = stringResource(R.string.delete_log_confirmation_title),
        confirmButtonText = R.string.action_delete,
        icon = {
            DeleteIcon(contentDescription = null)
        },
        onSubmit = onDelete,
        onClose = onDismissRequest,
    ) {
        Text(text = stringResource(R.string.delete_log_confirmation_body))
    }
}

@Preview
@Composable
fun DeleteLogAlertDialogPreview() {
    DeleteLogAlertDialog(onDismissRequest = {}, onDelete = {})
}
