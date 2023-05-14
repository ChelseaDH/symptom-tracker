import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.symptomtracker.R
import com.example.symptomtracker.data.movement.StoolType
import com.example.symptomtracker.data.movement.getDescription
import com.example.symptomtracker.data.movement.getDisplayName
import com.example.symptomtracker.ui.AppViewModelProvider
import com.example.symptomtracker.ui.components.*
import com.example.symptomtracker.ui.movement.MovementEntryViewModel
import com.example.symptomtracker.ui.movement.MovementUiState
import com.example.symptomtracker.ui.theme.SymptomTrackerTheme
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMovementScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MovementEntryViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    Scaffold(
        topBar = {
            SymptomTrackerTopAppBar(
                title = stringResource(R.string.log_movement_title),
                canNavigateBack = true,
                navigateUp = navigateBack,
                actions = {
                    TextButton(onClick = {
                        viewModel.insertMovementLog()
                        navigateBack()
                    }) {
                        Text(text = stringResource(R.string.save_button_text))
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
    uiState: MovementUiState,
    onChosenStoolTypeUpdated: (StoolType) -> Unit,
    onDateChanged: (DateInputFields) -> Unit,
    onTimeChanged: (TimeInputFields) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier
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
        Divider()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovementKey(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = stringResource(R.string.stool_type_key),
            style = MaterialTheme.typography.titleMedium)
        LazyColumn(modifier = modifier) {
            items(items = StoolType.values()) { stoolType ->
                ListItem(
                    headlineText = { Text(text = stoolType.getDisplayName()) },
                    supportingText = { Text(text = stoolType.getDescription()) },
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
            uiState = MovementUiState(Calendar.getInstance()),
            onChosenStoolTypeUpdated = {},
            onDateChanged = {},
            onTimeChanged = {},
        )
    }
}