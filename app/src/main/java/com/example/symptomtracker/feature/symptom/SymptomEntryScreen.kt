import androidx.annotation.StringRes
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
import com.example.symptomtracker.R
import com.example.symptomtracker.core.designsystem.SymptomTrackerTheme
import com.example.symptomtracker.core.designsystem.component.DateInputFields
import com.example.symptomtracker.core.designsystem.component.DateTimeInput
import com.example.symptomtracker.core.designsystem.component.DateTimeInputRow
import com.example.symptomtracker.core.designsystem.component.OutlinedInputTextFieldWithDropdown
import com.example.symptomtracker.core.designsystem.component.TimeInputFields
import com.example.symptomtracker.core.domain.model.Severity
import com.example.symptomtracker.core.domain.model.Symptom
import com.example.symptomtracker.core.domain.model.SymptomWithSeverity
import com.example.symptomtracker.feature.symptom.AbstractSymptomEntryViewModel
import com.example.symptomtracker.feature.symptom.SearchState
import com.example.symptomtracker.feature.symptom.SymptomEntryUiState
import com.example.symptomtracker.ui.SymptomTrackerTopAppBar
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
internal fun SymptomEntryScreen(
    navigateBack: () -> Unit,
    @StringRes titleId: Int,
    modifier: Modifier = Modifier,
    viewModel: AbstractSymptomEntryViewModel,
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            SymptomTrackerTopAppBar(
                title = stringResource(titleId),
                canNavigateBack = true,
                navigateUp = navigateBack,
                actions = {
                    TextButton(onClick = {
                        coroutineScope.launch {
                            viewModel.submit()
                            navigateBack()
                        }
                    }) {
                        Text(text = stringResource(R.string.action_save))
                    }
                }
            )
        }
    ) { innerPadding ->
        SymptomEntryBody(
            uiState = viewModel.uiState,
            onSymptomNameUpdated = viewModel::updateSearchInput,
            onCreateSymptom = {
                coroutineScope.launch {
                    viewModel.createNewSymptomFromInput()
                }
            },
            onClearInput = viewModel::clearSearchAndSeverity,
            onSelectedSymptomUpdated = viewModel::updateSelectedSearchSymptom,
            onSelectedSeverityUpdated = viewModel::updateSelectedSeverity,
            onRemoveSymptomFromLog = viewModel::removeSymptom,
            onAddSymptomToLog = viewModel::addSymptomWithSeverity,
            onDateChanged = viewModel::updateDate,
            onTimeChanged = viewModel::updateTime,
            modifier = modifier.padding(innerPadding)
        )
    }
}

@Composable
fun SymptomEntryBody(
    uiState: SymptomEntryUiState,
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
            searchState = uiState.searchState,
            selectedSeverity = uiState.selectedSeverity,
            dateTimeInput = uiState.dateTimeInput,
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
            symptomList = uiState.selectedSymptoms,
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
        items(items = symptomList, key = { it.symptom.id }) { item ->
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
    searchState: SearchState,
    selectedSeverity: Severity?,
    dateTimeInput: DateTimeInput,
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
            searchState = searchState,
            onSymptomNameUpdated = onSymptomNameUpdated,
            onCreateSymptom = onCreateSymptom,
            onClearInput = onClearInput,
            onSelectedSymptomUpdated = onSelectedSymptomUpdated
        )
        FormSeverityInput(
            severity = selectedSeverity,
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
    searchState: SearchState,
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
            availableOptions = searchState.results,
            textValue = searchState.input,
            onTextValueUpdated = onSymptomNameUpdated,
            canCreateOption = searchState.canCreateNewSymptom,
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
            uiState = SymptomEntryUiState(
                selectedSymptoms = listOf(
                    SymptomWithSeverity(Symptom(2, "Fatigue"), Severity.MODERATE),
                ),
                searchState = SearchState(
                    results = listOf(
                        Symptom(1, "Bloating"),
                        Symptom(2, "Fatigue"),
                        Symptom(3, "Nausea")
                    )
                ),
                selectedSeverity = Severity.MILD,
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
