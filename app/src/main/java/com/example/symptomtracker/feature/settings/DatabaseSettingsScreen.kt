package com.example.symptomtracker.feature.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.symptomtracker.R
import com.example.symptomtracker.ui.SymptomTrackerTopAppBar

@Composable
fun DatabaseSettingsRoute(
    navigateBack: () -> Unit,
    viewModel: DatabaseBackupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    DatabaseSettingsScreen(
        navigateBack = navigateBack,
        uiState = uiState,
        onExportDatabase = viewModel::exportDatabase,
        onImportDatabase = viewModel::importDatabase,
        onResetState = viewModel::resetUiState,
    )
}

@Composable
internal fun DatabaseSettingsScreen(
    navigateBack: () -> Unit,
    uiState: DatabaseBackupUiState,
    onExportDatabase: () -> Unit,
    onImportDatabase: (Uri) -> Unit,
    onResetState: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showFilePicker by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(contract = OpenDatabaseDocumentContract()) { uri ->
        if (uri != null) {
            onImportDatabase(uri)
        }
    }

    Scaffold(
        topBar = {
            SymptomTrackerTopAppBar(
                title = "Database",
                canNavigateBack = true,
                navigateUp = navigateBack,
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            ListItem(
                headlineContent = { Text(text = "Export database") },
                modifier = Modifier.clickable { onExportDatabase() },
                leadingContent = {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_file_download_24),
                        contentDescription = "Export database",
                    )
                }
            )
            ListItem(
                headlineContent = { Text(text = "Import database") },
                modifier = Modifier.clickable { showFilePicker = true },
                leadingContent = {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_file_upload_24),
                        contentDescription = "Import database",
                    )
                }
            )
        }

        if (showFilePicker) {
            launcher.launch(Unit)
            showFilePicker = false
        }

        LaunchedEffect(uiState) {
            when {
                uiState.loading -> {
                    snackbarHostState.showSnackbar(
                        message = "Exporting database...",
                        duration = SnackbarDuration.Indefinite
                    )
                }

                uiState.result != null -> {
                    snackbarHostState.showSnackbar(
                        message = if (uiState.result is Result.Success) "Operation successful." else "Operation failed.",
                        duration = SnackbarDuration.Short
                    )
                    onResetState()
                }
            }
        }
    }
}

@Preview
@Composable
fun DatabaseSettingsScreenPreview() {
    DatabaseSettingsScreen(
        navigateBack = {},
        uiState = DatabaseBackupUiState(loading = false),
        onExportDatabase = {},
        onImportDatabase = {},
        onResetState = {},
    )
}
