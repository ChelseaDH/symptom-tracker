import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.symptomtracker.R
import com.example.symptomtracker.core.designsystem.SymptomTrackerTheme
import com.example.symptomtracker.core.designsystem.component.DateTimeInput
import com.example.symptomtracker.core.designsystem.component.DateTimeInputRow
import com.example.symptomtracker.core.designsystem.component.FilledTonalButtonWithIcon
import com.example.symptomtracker.core.designsystem.component.LabelledOutlinedTextInputFieldWithDropdown
import com.example.symptomtracker.core.designsystem.icon.AddIcon
import com.example.symptomtracker.core.designsystem.icon.DeleteIcon
import com.example.symptomtracker.core.domain.model.Severity
import com.example.symptomtracker.core.domain.model.Symptom
import com.example.symptomtracker.core.domain.model.SymptomWithSeverity
import com.example.symptomtracker.feature.symptom.AbstractSymptomEntryViewModel
import com.example.symptomtracker.feature.symptom.SearchState
import com.example.symptomtracker.feature.symptom.SymptomEntryEvent
import com.example.symptomtracker.feature.symptom.SymptomEntryUiState
import com.example.symptomtracker.ui.SymptomTrackerTopAppBar

@Composable
internal fun SymptomEntryScreen(
    navigateBack: () -> Unit,
    @StringRes titleId: Int,
    modifier: Modifier = Modifier,
    viewModel: AbstractSymptomEntryViewModel,
) {
    Scaffold(
        topBar = {
            SymptomTrackerTopAppBar(
                title = stringResource(titleId),
                canNavigateBack = true,
                navigateUp = navigateBack,
                actions = {
                    TextButton(onClick = {
                        viewModel.handleEvent(SymptomEntryEvent.Submit)
                        navigateBack()
                    }) {
                        Text(text = stringResource(R.string.action_save))
                    }
                },
            )
        },
    ) { innerPadding ->
        SymptomEntryBody(
            uiState = viewModel.uiState,
            eventSink = viewModel::handleEvent,
            modifier = modifier.padding(innerPadding),
        )
    }
}

@Composable
fun SymptomEntryBody(
    uiState: SymptomEntryUiState,
    eventSink: (SymptomEntryEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(all = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        LogSymptomForm(
            searchState = uiState.searchState,
            selectedSeverity = uiState.selectedSeverity,
            dateTimeInput = uiState.dateTimeInput,
            eventSink = eventSink,
        )
        HorizontalDivider()
        SymptomLogList(
            symptomList = uiState.selectedSymptoms,
            onDeleteItem = { eventSink(SymptomEntryEvent.RemoveSymptom(it)) },
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
                        DeleteIcon(contentDescription = stringResource(R.string.delete_item_cd))
                    }
                },
                modifier = modifier,
            )
        }
    }
}

@Composable
fun LogSymptomForm(
    searchState: SearchState,
    selectedSeverity: Severity?,
    dateTimeInput: DateTimeInput,
    eventSink: (SymptomEntryEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        DateTimeInputRow(
            dateTimeInput = dateTimeInput,
            onDateChanged = { eventSink(SymptomEntryEvent.UpdateDate(it)) },
            onTimeChanged = { eventSink(SymptomEntryEvent.UpdateTime(it)) },
        )
        LabelledOutlinedTextInputFieldWithDropdown(
            label = stringResource(R.string.name_input_label),
            value = searchState.input,
            availableOptions = searchState.results,
            canCreateOption = searchState.canCreateNewSymptom,
            onValueChange = { eventSink(SymptomEntryEvent.UpdateSearchInput(it)) },
            getOptionDisplayName = { it.name },
            onCreateOption = { eventSink(SymptomEntryEvent.CreateNewSymptomFromInput) },
            onClearInput = { eventSink(SymptomEntryEvent.ClearSearchAndSeverity) },
            onChosenOptionUpdated = { eventSink(SymptomEntryEvent.UpdateSelectedSearchSymptom(it)) },
            modifier = Modifier.fillMaxWidth(),
        )
        FormSeverityInput(
            severity = selectedSeverity,
            onSelectionUpdated = { eventSink(SymptomEntryEvent.UpdateSelectedSeverity(it)) },
        )
        FilledTonalButtonWithIcon(
            textId = R.string.action_add_to_log,
            icon = {
                AddIcon(
                    contentDescription = stringResource(id = R.string.add_symptom_cd),
                    modifier = Modifier.padding(end = 8.dp),
                )
            },
            onClick = { eventSink(SymptomEntryEvent.AddSymptomWithSeverity) },
            modifier = Modifier.fillMaxWidth(),
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
            text = stringResource(R.string.severity_input_label),
            modifier = Modifier.padding(horizontal = 8.dp),
            style = MaterialTheme.typography.labelLarge,
        )
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Severity.entries.forEach {
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
                        Symptom(1, "Bloating"), Symptom(2, "Fatigue"), Symptom(3, "Nausea")
                    ),
                ),
                selectedSeverity = Severity.MILD, dateTimeInput = DateTimeInput(),
            ),
            eventSink = {},
        )
    }
}
