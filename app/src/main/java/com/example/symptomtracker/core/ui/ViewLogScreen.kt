package com.example.symptomtracker.core.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.symptomtracker.R
import com.example.symptomtracker.core.designsystem.component.Dialog
import com.example.symptomtracker.core.designsystem.icon.DeleteIcon
import com.example.symptomtracker.core.designsystem.icon.EditIcon
import com.example.symptomtracker.core.domain.model.FoodItem
import com.example.symptomtracker.core.domain.model.FoodLog
import com.example.symptomtracker.core.domain.model.Log
import com.example.symptomtracker.ui.SymptomTrackerTopAppBar
import java.time.OffsetDateTime

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
        is ViewLogUiState.Loading, ViewLogUiState.Empty -> ViewLogsScreen(
            navigateBack = navigateBack,
            showActions = false,
        )

        is ViewLogUiState.Data -> {
            val openDeleteDialog = remember { mutableStateOf(false) }

            ViewLogsScreen(
                navigateBack = navigateBack,
                onEdit = onEdit,
                onCopy = onCopy,
                onDelete = { openDeleteDialog.value = true }
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun ViewLogsScreen(
    navigateBack: () -> Unit,
    showActions: Boolean = true,
    onEdit: (() -> Unit)? = null,
    onCopy: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    Scaffold(
        topBar = {
            SymptomTrackerTopAppBar(
                title = "",
                canNavigateBack = true,
                navigateUp = navigateBack,
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column {
                content()
            }

            if (showActions) {
                HorizontalFloatingToolbar(
                    expanded = true,
                    modifier = Modifier
                        .align(alignment = Alignment.BottomCenter),
                    colors = FloatingToolbarDefaults.vibrantFloatingToolbarColors(),
                    floatingActionButton = {
                        if (onEdit !== null) {
                            FloatingToolbarDefaults.VibrantFloatingActionButton(onClick = onEdit) {
                                EditIcon(contentDescription = null)
                            }
                        }
                    }
                ) {
                    if (onCopy !== null) {
                        IconButton(onClick = onCopy) {
                            Icon(
                                painter = painterResource(id = R.drawable.outline_content_copy_24),
                                contentDescription = null,
                            )
                        }
                    }
                    if (onDelete != null) {
                        IconButton(onClick = onDelete) {
                            DeleteIcon(contentDescription = null)
                        }
                    }
                }
            }
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
fun PagePreview() {
    val state = ViewLogUiState.Data(
        log = FoodLog(
            id = 1L,
            date = OffsetDateTime.now(),
            items = listOf(
                FoodItem(name = "Apple"),
                FoodItem(name = "Banana")
            ),
        )
    )

    ViewLogScreen(
        navigateBack = {},
        uiState = state,
        title = R.string.add_food_text,
        deleteLog = { },
        onEdit = { },
        onCopy = { },
        bodyContent = {
            it.items.forEach { item ->
                ListItem(headlineContent = { Text(text = item.name) })
            }
        })
}

@Preview
@Composable
fun DeleteLogAlertDialogPreview() {
    DeleteLogAlertDialog(onDismissRequest = {}, onDelete = {})
}
