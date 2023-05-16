package com.example.symptomtracker.ui.movement

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
import com.example.symptomtracker.data.movement.MovementLog
import com.example.symptomtracker.data.movement.StoolType
import com.example.symptomtracker.data.movement.getDescription
import com.example.symptomtracker.data.movement.getDisplayName
import com.example.symptomtracker.ui.AppViewModelProvider
import com.example.symptomtracker.ui.theme.SymptomTrackerTheme
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovementLogListScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MovementLogListViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    Scaffold(
        topBar = {
            SymptomTrackerTopAppBar(
                title = stringResource(R.string.movement_logs_title),
                canNavigateBack = true,
                navigateUp = navigateBack,
            )
        },
        modifier = modifier
    ) { innerPadding ->
        MovementLogList(
            movementLogs = viewModel.uiState.movementLogs,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun MovementLogList(movementLogs: List<MovementLog>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items = movementLogs) {
            MovementLogCard(movementLog = it)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovementLogCard(movementLog: MovementLog, modifier: Modifier = Modifier) {
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
                text = movementLog.date.toString(),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding()
            )
            Divider()
            ListItem(
                headlineText = { Text(text = movementLog.stoolType.getDisplayName()) },
                supportingText = { Text(text = movementLog.stoolType.getDescription()) },
            )
        }
    }
}

@Composable
@Preview(showSystemUi = true)
fun AddFoodScreenPreview() {
    SymptomTrackerTheme {
        MovementLogList(
            movementLogs = listOf(
                MovementLog(symptomLogId = 1,
                    date = Date(),
                    stoolType = StoolType.SEVERE_CONSTIPATION),
                MovementLog(symptomLogId = 2,
                    date = Date(),
                    stoolType = StoolType.MILD_CONSTIPATION)
            ))
    }
}