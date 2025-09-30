package com.example.symptomtracker.feature.movement

import androidx.annotation.StringRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.symptomtracker.R
import com.example.symptomtracker.core.designsystem.SymptomTrackerTheme
import com.example.symptomtracker.core.designsystem.component.DateTimeInput
import com.example.symptomtracker.core.designsystem.component.DateTimeInputRow
import com.example.symptomtracker.core.designsystem.component.LabelledOutlinedReadOnlyDropdown
import com.example.symptomtracker.core.domain.model.StoolType
import com.example.symptomtracker.core.domain.model.getDescription
import com.example.symptomtracker.core.domain.model.getDisplayName
import com.example.symptomtracker.ui.SymptomTrackerTopAppBar
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MovementEntryScreen(
    navigateBack: () -> Unit,
    @StringRes titleId: Int,
    modifier: Modifier = Modifier,
    viewModel: AbstractMovementEntryViewModel,
) {
    Scaffold(
        topBar = {
            SymptomTrackerTopAppBar(
                title = stringResource(titleId),
                navigateUp = navigateBack,
                actions = {
                    TextButton(onClick = {
                        viewModel.handleEvent(MovementEntryEvent.Submit)
                        navigateBack()
                    }) {
                        Text(text = stringResource(R.string.action_save))
                    }
                })
        }, modifier = modifier
    ) { innerPadding ->
        MovementEntryBody(
            uiState = viewModel.uiState,
            eventSink = viewModel::handleEvent,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun MovementEntryBody(
    uiState: MovementEntryUiState,
    eventSink: (MovementEntryEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(all = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LogMovementForm(
            stoolType = uiState.chosenStoolType,
            dateTimeInput = uiState.dateTimeInput,
            onChosenStoolTypeUpdated = { eventSink(MovementEntryEvent.UpdateChosenStoolType(it)) },
            onDateChanged = { eventSink(MovementEntryEvent.UpdateDate(it)) },
            onTimeChanged = { eventSink(MovementEntryEvent.UpdateTime(it)) },
        )
        HorizontalDivider()
        MovementKey()
    }
}

@Composable
fun LogMovementForm(
    stoolType: StoolType?,
    dateTimeInput: DateTimeInput,
    onChosenStoolTypeUpdated: (StoolType) -> Unit,
    onDateChanged: (LocalDate) -> Unit,
    onTimeChanged: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        DateTimeInputRow(
            dateTimeInput = dateTimeInput,
            onDateChanged = onDateChanged,
            onTimeChanged = onTimeChanged,
        )
        LabelledOutlinedReadOnlyDropdown(
            label = stringResource(R.string.stool_type_label),
            value = stoolType?.let { "(%d) %s".format(it.type, it.getDisplayName()) } ?: "",
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.fillMaxWidth(),
        ) {
            StoolType.entries.forEach { stoolType ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "(%d) %s".format(
                                stoolType.type,
                                stoolType.getDisplayName()
                            )
                        )
                    },
                    onClick = {
                        onChosenStoolTypeUpdated(stoolType)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun MovementKey(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.stool_type_key),
            style = MaterialTheme.typography.titleMedium
        )
        LazyColumn(modifier = modifier) {
            items(items = StoolType.entries.toTypedArray()) { stoolType ->
                ListItem(
                    headlineContent = { Text(text = stoolType.getDisplayName()) },
                    supportingContent = { Text(text = stoolType.getDescription()) },
                    leadingContent = {
                        Avatar(text = stoolType.type.toString())
                    },
                )
            }
        }
    }
}

@Composable
fun Avatar(text: String) {
    Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
        val color = MaterialTheme.colorScheme.primaryContainer

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(color)
        }
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
@Preview(showSystemUi = true)
fun AddMovementScreenPreview() {
    SymptomTrackerTheme {
        MovementEntryBody(
            uiState = MovementEntryUiState(),
            eventSink = {},
        )
    }
}
