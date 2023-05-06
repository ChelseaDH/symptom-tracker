import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.symptomtracker.R
import com.example.symptomtracker.data.symptom.Symptom
import com.example.symptomtracker.ui.AppViewModelProvider
import com.example.symptomtracker.ui.components.OutlinedTextFieldWithDropdown
import com.example.symptomtracker.ui.symptom.SymptomEntryViewModel
import com.example.symptomtracker.ui.symptom.SymptomLogDetails
import com.example.symptomtracker.ui.symptom.SymptomUiState
import com.example.symptomtracker.ui.theme.SymptomTrackerTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSymptomScreen(
    navigateBack: () -> Unit,
    viewModel: SymptomEntryViewModel = viewModel(factory = AppViewModelProvider.Factory),
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
        SymptomEntryBody(symptomUiState = viewModel.uiState,
            onSymptomNameUpdated = viewModel::updateSelectedSymptomName,
            onCreateSymptom = { coroutineScope.launch { viewModel.insertSymptom() } },
            onClearInput = viewModel::clearSymptomInputs,
            onSelectedSymptomUpdated = viewModel::updateSelectedSymptom,
            onRemoveSymptomFromLog = viewModel::removeSymptomFromLog,
            onAddSymptomToLog = viewModel::addSymptomToLog,
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
    onRemoveSymptomFromLog: (Symptom) -> Unit,
    onAddSymptomToLog: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier
        .fillMaxWidth()
        .padding(all = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LogSymptomForm(availableSymptoms = symptomUiState.availableSymptoms,
            symptomName = symptomUiState.selectedSymptomName,
            canCreateSymptom = symptomUiState.canCreateSymptomFromInput,
            onSymptomNameUpdated = onSymptomNameUpdated,
            onCreateSymptom = onCreateSymptom,
            onClearInput = onClearInput,
            onSelectedSymptomUpdated = onSelectedSymptomUpdated,
            onAddSymptomToLog = onAddSymptomToLog
        )
        Divider()
        SymptomLogList(symptomList = symptomUiState.symptomLogDetails.symptoms,
            onDeleteItem = onRemoveSymptomFromLog)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymptomLogList(
    symptomList: List<Symptom>,
    onDeleteItem: (Symptom) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        items(items = symptomList, key = { it.symptomId }) { item ->
            ListItem(
                headlineText = { Text(text = item.name) },
                trailingContent = {
                    IconButton(onClick = { onDeleteItem(item) }) {
                        Icon(imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(
                                R.string.delete_item_cd))
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
    symptomName: String,
    canCreateSymptom: Boolean,
    onSymptomNameUpdated: (String) -> Unit,
    onCreateSymptom: () -> Unit,
    onClearInput: () -> Unit,
    onSelectedSymptomUpdated: (Symptom) -> Unit,
    onAddSymptomToLog: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextFieldWithDropdown(availableOptions = availableSymptoms,
            textValue = symptomName,
            onTextValueUpdated = onSymptomNameUpdated,
            canCreateOption = canCreateSymptom,
            onCreateOption = onCreateSymptom,
            onClearInput = onClearInput,
            onChosenOptionUpdated = onSelectedSymptomUpdated,
            textLabelId = R.string.add_symptom_text,
            getOptionDisplayName = { it.name }
        )
        FloatingActionButton(onClick = { onAddSymptomToLog() }) {
            Icon(imageVector = Icons.Default.Add,
                contentDescription = stringResource(id = R.string.add_symptom_cd))
        }
    }
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun AddSymptomScreenPreview() {
    SymptomTrackerTheme {
        SymptomEntryBody(symptomUiState = SymptomUiState(
            availableSymptoms = listOf(
                Symptom(1, "Bloating"),
                Symptom(2, "Fatigue"),
                Symptom(3, "Nausea")
            ),
            symptomLogDetails = SymptomLogDetails(symptoms = listOf(
                Symptom(2, "Fatigue"),
            ))
        ),
            onSymptomNameUpdated = {},
            onCreateSymptom = {},
            onClearInput = {},
            onSelectedSymptomUpdated = {},
            onRemoveSymptomFromLog = {},
            onAddSymptomToLog = {})
    }
}