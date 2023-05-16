import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.symptomtracker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToAddFood: () -> Unit,
    navigateToAddSymptom: () -> Unit,
    navigateToAddMovement: () -> Unit,
    navigateToViewFoodLogs: () -> Unit,
    navigateToViewSymptomLogs: () -> Unit,
    navigateToViewMovementLogs: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            SymptomTrackerTopAppBar(
                title = stringResource(R.string.home),
                canNavigateBack = false,
            )
        },
        modifier = modifier
    ) { innerPadding ->
        HomeBody(
            onAddFoodClick = navigateToAddFood,
            onAddSymptomClick = navigateToAddSymptom,
            onAddMovementClick = navigateToAddMovement,
            onViewFoodLogsClick = navigateToViewFoodLogs,
            onViewSymptomLogs = navigateToViewSymptomLogs,
            onViewMovementLogs = navigateToViewMovementLogs,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun HomeBody(
    onAddFoodClick: () -> Unit,
    onAddSymptomClick: () -> Unit,
    onAddMovementClick: () -> Unit,
    onViewFoodLogsClick: () -> Unit,
    onViewSymptomLogs: () -> Unit,
    onViewMovementLogs: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(all = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        QuickAdd(
            onAddFoodClick = onAddFoodClick,
            onAddSymptomClick = onAddSymptomClick,
            onAddMovementClick = onAddMovementClick
        )
        Divider()
        ViewLogs(
            onViewFoodLogs = onViewFoodLogsClick,
            onViewSymptomLogs = onViewSymptomLogs,
            onViewMovementLogs = onViewMovementLogs
        )
    }
}

@Composable
fun QuickAdd(
    onAddFoodClick: () -> Unit,
    onAddSymptomClick: () -> Unit,
    onAddMovementClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.quick_add_title),
            style = MaterialTheme.typography.titleMedium
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ExtendedFloatingActionButton(
                onClick = { onAddFoodClick() },
                text = { Text(text = stringResource(R.string.add_food_text)) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_food_cd)
                    )
                }
            )
            ExtendedFloatingActionButton(
                onClick = { onAddSymptomClick() },
                text = { Text(text = stringResource(R.string.add_symptom_text)) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_symptom_cd)
                    )
                }
            )
            ExtendedFloatingActionButton(
                onClick = { onAddMovementClick() },
                text = { Text(text = stringResource(R.string.add_movement_text)) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_movement_cd)
                    )
                }
            )
        }
    }
}

@Composable
fun ViewLogs(
    onViewFoodLogs: () -> Unit,
    onViewSymptomLogs: () -> Unit,
    onViewMovementLogs: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "View logs",
            style = MaterialTheme.typography.titleMedium
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ExtendedFloatingActionButton(
                onClick = { onViewFoodLogs() }
            ) {
                Text(text = stringResource(R.string.add_food_text))
            }
            ExtendedFloatingActionButton(
                onClick = { onViewSymptomLogs() }
            ) {
                Text(text = stringResource(R.string.add_symptom_text))
            }
            ExtendedFloatingActionButton(
                onClick = { onViewMovementLogs() }
            ) {
                Text(text = stringResource(R.string.add_movement_text))
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        navigateToAddFood = {},
        navigateToAddSymptom = {},
        navigateToAddMovement = {},
        navigateToViewFoodLogs = {},
        navigateToViewSymptomLogs = {},
        navigateToViewMovementLogs = {},
    )
}