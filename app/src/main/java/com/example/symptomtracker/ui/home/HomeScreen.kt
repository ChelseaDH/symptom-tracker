package com.example.symptomtracker.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.symptomtracker.R
import com.example.symptomtracker.SymptomTrackerTopAppBar
import com.example.symptomtracker.ui.AppViewModelProvider
import kotlinx.coroutines.launch

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
    viewModel: HomeScreenViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    fun onQuickAddNavigation() {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            viewModel.updateBottomSheetVisibility(false)
        }
    }

    Scaffold(
        topBar = {
            SymptomTrackerTopAppBar(
                title = stringResource(R.string.home),
                canNavigateBack = false,
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.updateBottomSheetVisibility(true) }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add)
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        HomeBody(
            onViewFoodLogsClick = navigateToViewFoodLogs,
            onViewSymptomLogs = navigateToViewSymptomLogs,
            onViewMovementLogs = navigateToViewMovementLogs,
            modifier = Modifier.padding(innerPadding)
        )
        if (viewModel.uiState.showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.updateBottomSheetVisibility(false) },
                sheetState = sheetState,
            )
            {
                QuickAdd(
                    onAddFoodClick = {
                        onQuickAddNavigation()
                        navigateToAddFood()
                    },
                    onAddSymptomClick = {
                        onQuickAddNavigation()
                        navigateToAddSymptom()
                    },
                    onAddMovementClick = {
                        onQuickAddNavigation()
                        navigateToAddMovement()
                    }
                )
            }
        }
    }
}

@Composable
fun HomeBody(
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
        ViewLogs(
            onViewFoodLogs = onViewFoodLogsClick,
            onViewSymptomLogs = onViewSymptomLogs,
            onViewMovementLogs = onViewMovementLogs
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuickAdd(
    onAddFoodClick: () -> Unit,
    onAddSymptomClick: () -> Unit,
    onAddMovementClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.quick_add_title),
            style = MaterialTheme.typography.titleMedium
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            maxItemsInEachRow = 2
        ) {
            ExtendedFloatingActionButton(
                onClick = { onAddFoodClick() },
                text = { Text(text = stringResource(R.string.add_food_text)) },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_nutrition_24),
                        contentDescription = stringResource(R.string.add_food_cd)
                    )
                },
                modifier = Modifier.weight(1f),
                elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
            )
            ExtendedFloatingActionButton(
                onClick = { onAddSymptomClick() },
                text = { Text(text = stringResource(R.string.add_symptom_text)) },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_symptoms_24),
                        contentDescription = stringResource(R.string.add_symptom_cd)
                    )
                },
                modifier = Modifier.weight(1f),
                elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
            )
            ExtendedFloatingActionButton(
                onClick = { onAddMovementClick() },
                text = { Text(text = stringResource(R.string.add_movement_text)) },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_wc_24),
                        contentDescription = stringResource(R.string.add_movement_cd)
                    )
                },
                modifier = Modifier.weight(1f),
                elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
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

@Preview
@Composable
fun QuickAddPreview() {
    QuickAdd(
        onAddFoodClick = {},
        onAddSymptomClick = {},
        onAddMovementClick = {},
    )
}