import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.symptomtracker.R
import com.example.symptomtracker.SymptomTrackerTopAppBar
import com.example.symptomtracker.data.symptom.Severity
import com.example.symptomtracker.data.symptom.Symptom
import com.example.symptomtracker.data.symptom.SymptomWithSeverity
import com.example.symptomtracker.ui.components.DateInputFields
import com.example.symptomtracker.ui.components.DateTimeInput
import com.example.symptomtracker.ui.components.DateTimeInputRow
import com.example.symptomtracker.ui.components.OutlinedInputTextFieldWithDropdown
import com.example.symptomtracker.ui.components.TimeInputFields
import com.example.symptomtracker.ui.symptom.SymptomEntryViewModel
import com.example.symptomtracker.ui.symptom.SymptomInput
import com.example.symptomtracker.ui.symptom.SymptomLogDetails
import com.example.symptomtracker.ui.symptom.SymptomUiState
import com.example.symptomtracker.ui.theme.SymptomTrackerTheme
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun AddSymptomScreen(
    navigateBack: () -> Unit,
    viewModel: SymptomEntryViewModel = hiltViewModel(),
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            SymptomTrackerTopAppBar(
                title = stringResource(R.string.log_symptom_title),
                canNavigateBack = true,
                navigateUp = navigateBack,
                actions = {
                    TextButton(onClick = {
                        coroutineScope.launch {
                            viewModel.insertSymptomLog()
                            navigateBack()
                        }
                    }) {
                        Text(text = stringResource(R.string.save_button_text))
                    }
                }
            )
        }
    ) { innerPadding ->
        SymptomEntryBody(
            symptomUiState = viewModel.uiState,
            onSymptomNameUpdated = viewModel::updateSelectedSymptomName,
            onCreateSymptom = viewModel::insertSymptom,
            onClearInput = viewModel::clearSymptomInputs,
            onSelectedSymptomUpdated = viewModel::updateSelectedSymptom,
            onSelectedSeverityUpdated = viewModel::updateSelectedSeverity,
            onRemoveSymptomFromLog = viewModel::removeSymptomFromLog,
            onAddSymptomToLog = viewModel::addSymptomWithSeverityToLog,
            onDateChanged = viewModel::updateDate,
            onTimeChanged = viewModel::updateTime,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun SymptomEntryBody(
    symptomUiState: SymptomUiState,
    onSymptomNameUpdated: (String) -> Unit,
    onCreateSymptom: () -> Unit,
    onClearInput: () -> Unit,
    onSelectedSymptomUpdated: (Symptom) -> Unit,
    onSelectedSeverityUpdated: (Severity) -> Unit,
    onRemoveSymptomFromLog: (SymptomWithSeverity) -> Unit,
    onAddSymptomToLog: () -> Unit,
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
        LogSymptomForm(
            availableSymptoms = symptomUiState.availableSymptoms,
            symptomInput = symptomUiState.symptomInput,
            dateTimeInput = symptomUiState.dateTimeInput,
            canCreateSymptom = symptomUiState.canCreateSymptomFromInput,
            onSymptomNameUpdated = onSymptomNameUpdated,
            onCreateSymptom = onCreateSymptom,
            onClearInput = onClearInput,
            onSelectedSymptomUpdated = onSelectedSymptomUpdated,
            onSelectedSeverityUpdated = onSelectedSeverityUpdated,
            onAddSymptomToLog = onAddSymptomToLog,
            onDateChanged = onDateChanged,
            onTimeChanged = onTimeChanged,
        )
        HorizontalDivider()
        SymptomLogList(
            symptomList = symptomUiState.symptomLogDetails.symptomsWithSeverity,
            onDeleteItem = onRemoveSymptomFromLog
        )
    }
}

@Composable
fun SymptomLogList(
    symptomList: List<SymptomWithSeverity>,
    onDeleteItem: (SymptomWithSeverity) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        items(items = symptomList, key = { it.symptom.symptomId }) { item ->
            ListItem(
                headlineContent = { Text(text = item.symptom.name) },
                supportingContent = { Text(text = item.severity.displayName) },
                trailingContent = {
                    IconButton(onClick = { onDeleteItem(item) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(
                                R.string.delete_item_cd
                            )
                        )
                    }
                },
                modifier = modifier
            )
        }
    }
}

@Composable
fun LogSymptomForm(
    availableSymptoms: List<Symptom>,
    symptomInput: SymptomInput,
    dateTimeInput: DateTimeInput,
    canCreateSymptom: Boolean,
    onSymptomNameUpdated: (String) -> Unit,
    onCreateSymptom: () -> Unit,
    onClearInput: () -> Unit,
    onSelectedSymptomUpdated: (Symptom) -> Unit,
    onSelectedSeverityUpdated: (Severity) -> Unit,
    onDateChanged: (DateInputFields) -> Unit,
    onTimeChanged: (TimeInputFields) -> Unit,
    onAddSymptomToLog: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        DateTimeInputRow(
            dateTimeInput = dateTimeInput,
            onDateChanged = onDateChanged,
            onTimeChanged = onTimeChanged,
            labelOnTextField = true,
        )
        FormNameInput(
            availableSymptoms = availableSymptoms,
            symptomName = symptomInput.name,
            canCreateSymptom = canCreateSymptom,
            onSymptomNameUpdated = onSymptomNameUpdated,
            onCreateSymptom = onCreateSymptom,
            onClearInput = onClearInput,
            onSelectedSymptomUpdated = onSelectedSymptomUpdated
        )
        FormSeverityInput(
            severity = symptomInput.severity,
            onSelectionUpdated = onSelectedSeverityUpdated
        )
        FilledTonalButton(
            onClick = { onAddSymptomToLog() },
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(id = R.string.add_symptom_cd),
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(text = "Add to log")
        }
    }
}

@Composable
fun FormNameInput(
    availableSymptoms: List<Symptom>,
    symptomName: String,
    canCreateSymptom: Boolean,
    onSymptomNameUpdated: (String) -> Unit,
    onCreateSymptom: () -> Unit,
    onClearInput: () -> Unit,
    onSelectedSymptomUpdated: (Symptom) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = stringResource(R.string.name_input_label)
        )
        OutlinedInputTextFieldWithDropdown(
            availableOptions = availableSymptoms,
            textValue = symptomName,
            onTextValueUpdated = onSymptomNameUpdated,
            canCreateOption = canCreateSymptom,
            onCreateOption = onCreateSymptom,
            onClearInput = onClearInput,
            onChosenOptionUpdated = onSelectedSymptomUpdated,
            textLabelId = null,
            getOptionDisplayName = { it.name },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun FormSeverityInput(
    severity: Severity?,
    onSelectionUpdated: (Severity) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = stringResource(R.string.severity_input_label)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Severity.values().forEach {
                AssistChip(
                    onClick = { onSelectionUpdated(it) },
                    label = { Text(text = it.displayName) },
                    colors = if (severity == it) {
                        AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    } else {
                        AssistChipDefaults.assistChipColors()
                    }
                )
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun AddSymptomScreenPreview() {
    SymptomTrackerTheme {
        SymptomEntryBody(
            symptomUiState = SymptomUiState(
                availableSymptoms = listOf(
                    Symptom(1, "Bloating"),
                    Symptom(2, "Fatigue"),
                    Symptom(3, "Nausea")
                ),
                symptomLogDetails = SymptomLogDetails(
                    symptomsWithSeverity = listOf(
                        SymptomWithSeverity(Symptom(2, "Fatigue"), Severity.MODERATE),
                    )
                ),
                symptomInput = SymptomInput(severity = Severity.MILD),
                dateTimeInput = DateTimeInput(Calendar.getInstance())
            ),
            onSymptomNameUpdated = {},
            onCreateSymptom = {},
            onClearInput = {},
            onSelectedSymptomUpdated = {},
            onSelectedSeverityUpdated = {},
            onRemoveSymptomFromLog = {},
            onAddSymptomToLog = {},
            onDateChanged = {},
            onTimeChanged = {},
        )
    }
}
