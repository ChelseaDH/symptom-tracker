package com.example.symptomtracker.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.symptomtracker.R
import com.example.symptomtracker.SymptomTrackerTopAppBar
import com.example.symptomtracker.data.Log
import com.example.symptomtracker.data.food.FoodLog
import com.example.symptomtracker.data.food.FoodLogWithItems
import com.example.symptomtracker.data.food.Item
import com.example.symptomtracker.data.movement.MovementLog
import com.example.symptomtracker.data.movement.StoolType
import com.example.symptomtracker.data.movement.getDisplayName
import com.example.symptomtracker.data.symptom.Symptom
import com.example.symptomtracker.data.symptom.SymptomLog
import com.example.symptomtracker.data.symptom.SymptomLogWithSymptoms
import com.example.symptomtracker.ui.AppViewModelProvider
import com.example.symptomtracker.ui.components.LogItemCard
import com.example.symptomtracker.ui.components.NoLogsFoundCard
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

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
            logs = viewModel.uiState.logs,
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
    logs: List<Log>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(all = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        ViewLogs(
            onViewFoodLogs = onViewFoodLogsClick,
            onViewSymptomLogs = onViewSymptomLogs,
            onViewMovementLogs = onViewMovementLogs
        )
        Timeline(logs = logs)
    }
}

@Composable
fun Timeline(
    logs: List<Log>,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.timeline_title),
            style = MaterialTheme.typography.titleMedium
        )
        if (logs.isEmpty()) {
            NoLogsFoundCard()
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items = logs) { log ->
                    when (log) {
                        is FoodLogWithItems -> LogItemCard(
                            icon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.outline_nutrition_24),
                                    contentDescription = stringResource(R.string.add_food_text)
                                )
                            },
                            title = stringResource(R.string.add_food_text),
                            date = log.getDate(),
                            dateTimeFormatter = DateTimeFormatter.ofPattern(stringResource(R.string.datetime_format_hh_mm_ss)),
                            supportingText = log.items.joinToString { it.name }
                        )

                        is SymptomLogWithSymptoms -> LogItemCard(
                            icon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.outline_symptoms_24),
                                    contentDescription = stringResource(R.string.add_symptom_text)
                                )
                            },
                            title = stringResource(R.string.add_symptom_text),
                            date = log.getDate(),
                            dateTimeFormatter = DateTimeFormatter.ofPattern(stringResource(R.string.datetime_format_hh_mm_ss)),
                            supportingText = log.items.joinToString { it.name }
                        )

                        is MovementLog -> LogItemCard(
                            icon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.outline_gastroenterology_24),
                                    contentDescription = stringResource(R.string.add_movement_text)
                                )
                            },
                            title = stringResource(R.string.add_movement_text),
                            date = log.getDate(),
                            dateTimeFormatter = DateTimeFormatter.ofPattern(stringResource(R.string.datetime_format_hh_mm_ss)),
                            supportingText = log.stoolType.getDisplayName(),
                        )
                    }
                }
            }
        }
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
                        painter = painterResource(id = R.drawable.outline_gastroenterology_24),
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

@Preview
@Composable
fun HomeBodyWithNoLogsPreview() {
    HomeBody(
        onViewFoodLogsClick = {},
        onViewSymptomLogs = {},
        onViewMovementLogs = {},
        logs = listOf(),
    )
}

@Preview
@Composable
fun HomeBodyWithLogsPreview() {
    HomeBody(
        onViewFoodLogsClick = {},
        onViewSymptomLogs = {},
        onViewMovementLogs = {},
        logs = listOf(
            FoodLogWithItems(
                log = FoodLog(1, OffsetDateTime.parse("2023-03-02T08:30:00+00:00")),
                items = listOf(
                    Item(1, "Banana"),
                    Item(2, "Oats"),
                    Item(3, "Yogurt"),
                )
            ),
            SymptomLogWithSymptoms(
                log = SymptomLog(1, OffsetDateTime.parse("2023-03-02T09:00:00+00:00")),
                items = listOf(
                    Symptom(1, "Bloating")
                )
            ),
            FoodLogWithItems(
                log = FoodLog(2, OffsetDateTime.parse("2023-03-02T13:15:00+00:00")),
                items = listOf(
                    Item(4, "Chicken"),
                    Item(5, "Rice"),
                    Item(6, "Peppers"),
                    Item(7, "Chorizo"),
                    Item(8, "Onion"),
                    Item(9, "Olive oil"),
                )
            ),
            MovementLog(
                movementLogId = 1,
                date = OffsetDateTime.parse("2023-03-02T14:10:00+00:00"),
                stoolType = StoolType.NORMAL_3,
            )
        )
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
