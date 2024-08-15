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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.symptomtracker.R
import com.example.symptomtracker.core.model.StoolType
import com.example.symptomtracker.core.model.getDescription
import com.example.symptomtracker.core.model.getDisplayName
import com.example.symptomtracker.core.ui.DateInputFields
import com.example.symptomtracker.core.ui.DateTimeInput
import com.example.symptomtracker.core.ui.DateTimeInputRow
import com.example.symptomtracker.core.ui.OutlinedReadonlyTextFieldWithDropdown
import com.example.symptomtracker.core.ui.SymptomTrackerTheme
import com.example.symptomtracker.core.ui.TimeInputFields
import com.example.symptomtracker.ui.SymptomTrackerTopAppBar
import java.util.Calendar

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
                canNavigateBack = true,
                navigateUp = navigateBack,
                actions = {
                    TextButton(onClick = {
                        viewModel.submit()
                        navigateBack()
                    }) {
                        Text(text = stringResource(R.string.action_save))
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        MovementEntryBody(
            uiState = viewModel.uiState,
            onChosenStoolTypeUpdated = viewModel::updateChosenStoolType,
            onDateChanged = viewModel::updateDate,
            onTimeChanged = viewModel::updateTime,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun MovementEntryBody(
    uiState: MovementEntryUiState,
    onChosenStoolTypeUpdated: (StoolType) -> Unit,
    onDateChanged: (DateInputFields) -> Unit,
    onTimeChanged: (TimeInputFields) -> Unit,
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
            onChosenStoolTypeUpdated = onChosenStoolTypeUpdated,
            onDateChanged = onDateChanged,
            onTimeChanged = onTimeChanged,
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
    onDateChanged: (DateInputFields) -> Unit,
    onTimeChanged: (TimeInputFields) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        DateTimeInputRow(
            dateTimeInput = dateTimeInput,
            onDateChanged = onDateChanged,
            onTimeChanged = onTimeChanged,
            labelOnTextField = true,
        )
        OutlinedReadonlyTextFieldWithDropdown(
            availableOptions = StoolType.values().asList(),
            getOptionDisplayName = { "(%d) %s".format(it.type, it.getDisplayName()) },
            chosenOption = stoolType,
            onChosenOptionUpdated = onChosenStoolTypeUpdated,
            textLabelId = R.string.stool_type_label,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun MovementKey(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.stool_type_key),
            style = MaterialTheme.typography.titleMedium
        )
        LazyColumn(modifier = modifier) {
            items(items = StoolType.values()) { stoolType ->
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
            uiState = MovementEntryUiState(Calendar.getInstance()),
            onChosenStoolTypeUpdated = {},
            onDateChanged = {},
            onTimeChanged = {},
        )
    }
}
