package com.example.symptomtracker.feature.view_symptom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.symptomtracker.R
import com.example.symptomtracker.core.database.model.SymptomLogWithSymptoms
import com.example.symptomtracker.core.ui.LogDateTime
import com.example.symptomtracker.core.ui.SymptomLogPreviewParameterProvider
import com.example.symptomtracker.ui.SymptomTrackerTopAppBar

@Composable
fun ViewSymptomRoute(
    navigateBack: () -> Unit,
    viewModel: ViewSymptomViewModel = hiltViewModel()
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
        ViewSymptomScreen(
            modifier = Modifier.padding(innerPadding),
            state = viewModel.uiState,
        )
    }
}

@Composable
internal fun ViewSymptomScreen(
    modifier: Modifier = Modifier,
    state: ViewSymptomUiState = ViewSymptomUiState.Loading,
) {
    Column(modifier = modifier.padding(8.dp)) {
        when (state) {
            ViewSymptomUiState.Loading -> Unit

            is ViewSymptomUiState.SymptomLog -> {
                ViewSymptomBody(
                    symptomLog = state.symptomLog,
                )
            }
        }
    }
}

@Composable
internal fun ViewSymptomBody(
    symptomLog: SymptomLogWithSymptoms,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.add_symptom_text),
                style = MaterialTheme.typography.headlineMedium,
            )
            LogDateTime(log = symptomLog)
        }
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            symptomLog.items.forEach { item ->
                ListItem(
                    headlineContent = { Text(text = item.name) }
                )
            }
        }
    }
}

@Preview
@Composable
fun ViewSymptomBodyPreview(
    @PreviewParameter(
        SymptomLogPreviewParameterProvider::class,
        limit = 1
    ) symptomLog: SymptomLogWithSymptoms
) {
    ViewSymptomBody(symptomLog = symptomLog)
}
