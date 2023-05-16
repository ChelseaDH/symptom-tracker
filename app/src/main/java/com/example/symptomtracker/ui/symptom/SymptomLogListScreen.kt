package com.example.symptomtracker.ui.symptom

import SymptomTrackerTopAppBar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.symptomtracker.R
import com.example.symptomtracker.data.symptom.*
import com.example.symptomtracker.ui.AppViewModelProvider
import com.example.symptomtracker.ui.theme.SymptomTrackerTheme
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymptomLogListScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SymptomLogListViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    Scaffold(
        topBar = {
            SymptomTrackerTopAppBar(
                title = stringResource(R.string.symptom_logs_title),
                canNavigateBack = true,
                navigateUp = navigateBack,
            )
        },
        modifier = modifier
    ) { innerPadding ->
        SymptomLogList(
            symptomLogs = viewModel.uiState.symptomLogs,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun SymptomLogList(symptomLogs: Map<SymptomLog, List<Symptom>>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items = symptomLogs.toList()) {
            SymptomLogCard(symptomLogWithSymptoms = it)
        }
    }
}

@Composable
fun SymptomLogCard(
    symptomLogWithSymptoms: Pair<SymptomLog, List<Symptom>>,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = symptomLogWithSymptoms.first.date.toString(),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding()
            )
            Divider()
            SymptomWithSeverityList(symptomsWithSeverity = symptomLogWithSymptoms.second)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymptomWithSeverityList(symptomsWithSeverity: List<Symptom>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        symptomsWithSeverity.forEach {
            ListItem(
                headlineText = { Text(text = it.name) },
                modifier = modifier
            )
        }
    }
}

@Composable
@Preview(showSystemUi = true)
fun AddFoodScreenPreview() {
    SymptomTrackerTheme {
        SymptomLogList(
            symptomLogs = mapOf(
                Pair(
                    SymptomLog(1, Date()),
                    listOf(Symptom(1, "Bloating"))
                )
            )
        )
    }
}