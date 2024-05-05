package com.example.symptomtracker.core.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.symptomtracker.core.model.Log
import com.example.symptomtracker.ui.SymptomTrackerTopAppBar

@Composable
fun <L : Log> ViewLogScreen(
    navigateBack: () -> Unit,
    uiState: ViewLogUiState<L>,
    @StringRes title: Int,
    bodyContent: @Composable ColumnScope.(L) -> Unit
) {
    Scaffold(
        topBar = {
            SymptomTrackerTopAppBar(
                title = "",
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp)
        ) {
            when (uiState) {
                is ViewLogUiState.Loading -> Unit

                is ViewLogUiState.Data -> {
                    ViewLogBody(
                        log = uiState.log,
                        title = title,
                        bodyContent = bodyContent,
                    )
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
