package com.example.symptomtracker.ui.symptom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.symptomtracker.R
import com.example.symptomtracker.SymptomTrackerTopAppBar
import com.example.symptomtracker.data.symptom.Symptom
import com.example.symptomtracker.data.symptom.SymptomLog
import com.example.symptomtracker.data.symptom.SymptomLogWithSymptoms
import com.example.symptomtracker.ui.AppViewModelProvider
import com.example.symptomtracker.ui.theme.SymptomTrackerTheme
import java.time.OffsetDateTime

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
fun SymptomLogList(symptomLogs: List<SymptomLogWithSymptoms>, modifier: Modifier = Modifier) {
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
    symptomLogWithSymptoms: SymptomLogWithSymptoms,
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
                text = symptomLogWithSymptoms.getDate().toString(),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding()
            )
            HorizontalDivider()
            SymptomWithSeverityList(symptomsWithSeverity = symptomLogWithSymptoms.items)
        }
    }
}

@Composable
fun SymptomWithSeverityList(symptomsWithSeverity: List<Symptom>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        symptomsWithSeverity.forEach {
            ListItem(
                headlineContent = { Text(text = it.name) },
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
            symptomLogs = listOf(
                SymptomLogWithSymptoms(
                    SymptomLog(1, OffsetDateTime.now()),
                    listOf(Symptom(1, "Bloating"))
                )
            )
        )
    }
}
