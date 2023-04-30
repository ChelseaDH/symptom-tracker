import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.symptomtracker.R
import com.example.symptomtracker.ui.AppViewModelProvider
import com.example.symptomtracker.ui.symptom.SymptomDetails
import com.example.symptomtracker.ui.symptom.SymptomEntryViewModel
import com.example.symptomtracker.ui.symptom.SymptomType
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
                            viewModel.saveSymptom()
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
            symptomUiState = viewModel.symptomUiState,
            onSymptomValueChange = viewModel::updateUiState,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun SymptomEntryBody(
    symptomUiState: SymptomUiState,
    onSymptomValueChange: (SymptomDetails) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier
        .fillMaxWidth()
    ) {
        LogSymptomForm(symptomDetails = symptomUiState.symptomDetails,
            onValueChange = onSymptomValueChange)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogSymptomForm(
    symptomDetails: SymptomDetails,
    onValueChange: (SymptomDetails) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectorExpanded by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(all = 16.dp)
            .fillMaxWidth()) {
        ExposedDropdownMenuBox(
            expanded = selectorExpanded,
            onExpandedChange = { selectorExpanded = !selectorExpanded }
        ) {
            OutlinedTextField(value = symptomDetails.type, onValueChange = {}, label = {
                Text(text = stringResource(id = R.string.add_symptom_text),
                    color = MaterialTheme.colorScheme.onPrimaryContainer)
            }, modifier = Modifier.menuAnchor(), readOnly = true, trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = selectorExpanded)
            })
            ExposedDropdownMenu(
                expanded = selectorExpanded,
                onDismissRequest = { selectorExpanded = false }) {
                for (symptomValue in SymptomType.values()) {
                    DropdownMenuItem(text = { Text(text = symptomValue.printableString) },
                        onClick = {
                            onValueChange(symptomDetails.copy(type = symptomValue.printableString))
                            selectorExpanded = false
                        })
                }
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun AddSymptomScreenPreview() {
    SymptomTrackerTheme {
        SymptomEntryBody(symptomUiState = SymptomUiState(
            symptomDetails = SymptomDetails(
                id = 1,
                type = SymptomType.FATIGUE.printableString
            )
        ), onSymptomValueChange = {})
    }
}