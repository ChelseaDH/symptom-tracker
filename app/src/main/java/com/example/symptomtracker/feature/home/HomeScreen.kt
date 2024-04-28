package com.example.symptomtracker.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.symptomtracker.R
import com.example.symptomtracker.core.database.model.FoodLog
import com.example.symptomtracker.core.database.model.FoodLogWithItems
import com.example.symptomtracker.core.database.model.Item
import com.example.symptomtracker.core.database.model.MovementLog
import com.example.symptomtracker.core.database.model.Symptom
import com.example.symptomtracker.core.database.model.SymptomLog
import com.example.symptomtracker.core.database.model.SymptomLogWithSymptoms
import com.example.symptomtracker.core.model.Log
import com.example.symptomtracker.core.model.StoolType
import com.example.symptomtracker.core.model.getDisplayName
import com.example.symptomtracker.core.ui.DatePickerModal
import com.example.symptomtracker.core.ui.LogItemCard
import com.example.symptomtracker.core.ui.NoLogsFoundCard
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToAddFood: () -> Unit,
    navigateToAddSymptom: () -> Unit,
    navigateToAddMovement: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel = hiltViewModel(),
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    fun onQuickAddNavigation() {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            viewModel.updateBottomSheetVisibility(false)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.updateBottomSheetVisibility(true) }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add)
                )
            }
        }, modifier = modifier
    ) { innerPadding ->
        HomeBody(
            date = viewModel.uiState.date,
            isToday = viewModel.uiState.isToday,
            goToPreviousDate = viewModel::goToPreviousDay,
            goToNextDate = viewModel::goToNextDay,
            onDateChanged = viewModel::updateDate,
            logs = viewModel.uiState.logs,
            modifier = Modifier.padding(innerPadding)
        )
        if (viewModel.uiState.showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.updateBottomSheetVisibility(false) },
                sheetState = sheetState,
            ) {
                QuickAdd(onAddFoodClick = {
                    onQuickAddNavigation()
                    navigateToAddFood()
                }, onAddSymptomClick = {
                    onQuickAddNavigation()
                    navigateToAddSymptom()
                }, onAddMovementClick = {
                    onQuickAddNavigation()
                    navigateToAddMovement()
                })
            }
        }
    }
}

@Composable
fun HomeBody(
    date: OffsetDateTime,
    isToday: Boolean,
    goToPreviousDate: () -> Unit,
    goToNextDate: () -> Unit,
    onDateChanged: (Long) -> Unit,
    logs: List<Log>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        DateToggleRow(
            date = date,
            isToday = isToday,
            goToPreviousDate = goToPreviousDate,
            goToNextDate = goToNextDate,
            onDateChanged = onDateChanged,
        )
        Timeline(
            logs = logs,
            onLeftSwipe = goToNextDate,
            onRightSwipe = goToPreviousDate,
        )
    }
}

@Composable
fun DateToggleRow(
    date: OffsetDateTime,
    isToday: Boolean,
    goToPreviousDate: () -> Unit,
    goToNextDate: () -> Unit,
    onDateChanged: (Long) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    if (showDatePicker) {
        DatePickerModal(
            onDismissRequest = { showDatePicker = false },
            onDateSelected = onDateChanged,
            initialDate = date,
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(onClick = goToPreviousDate) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = stringResource(R.string.previous_day_cd)
            )
        }
        if (isToday) {
            Text(
                text = "Today",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth()
                    .padding(end = 48.dp)
                    .clickable { showDatePicker = true },
            )
        } else {
            Text(
                text = date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth()
                    .clickable { showDatePicker = true },
            )
            IconButton(onClick = goToNextDate) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = stringResource(R.string.next_day_cd)
                )
            }
        }
    }
}

@Composable
fun Timeline(
    logs: List<Log>,
    onLeftSwipe: () -> Unit,
    onRightSwipe: () -> Unit,
) {
    var swipeOffset by remember { mutableFloatStateOf(0f) }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.timeline_title),
            style = MaterialTheme.typography.titleMedium
        )
        Box(modifier = Modifier
            .fillMaxHeight()
            .pointerInput(Unit) {
                detectHorizontalDragGestures(onDragStart = { swipeOffset = 0f }, onDragEnd = {
                    if (swipeOffset < -200) {
                        onLeftSwipe()
                    } else if (swipeOffset > 200) {
                        onRightSwipe()
                    }
                }) { _, dragAmount -> swipeOffset += dragAmount }
            }) {
            if (logs.isEmpty()) {
                NoLogsFoundCard()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(items = logs) { log ->
                        when (log) {
                            is FoodLogWithItems -> LogItemCard(icon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.outline_nutrition_24),
                                    contentDescription = stringResource(R.string.add_food_text)
                                )
                            },
                                title = stringResource(R.string.add_food_text),
                                date = log.getDate(),
                                dateTimeFormatter = DateTimeFormatter.ofPattern(stringResource(R.string.datetime_format_hh_mm)),
                                supportingText = log.items.joinToString { it.name })

                            is SymptomLogWithSymptoms -> LogItemCard(icon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.outline_symptoms_24),
                                    contentDescription = stringResource(R.string.add_symptom_text)
                                )
                            },
                                title = stringResource(R.string.add_symptom_text),
                                date = log.getDate(),
                                dateTimeFormatter = DateTimeFormatter.ofPattern(stringResource(R.string.datetime_format_hh_mm)),
                                supportingText = log.items.joinToString { it.name })

                            is MovementLog -> LogItemCard(
                                icon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.outline_gastroenterology_24),
                                        contentDescription = stringResource(R.string.add_movement_text)
                                    )
                                },
                                title = stringResource(R.string.add_movement_text),
                                date = log.getDate(),
                                dateTimeFormatter = DateTimeFormatter.ofPattern(stringResource(R.string.datetime_format_hh_mm)),
                                supportingText = log.stoolType.getDisplayName(),
                            )
                        }
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

@Preview
@Composable
fun HomeBodyWithNoLogsPreview() {
    HomeBody(
        date = OffsetDateTime.parse("2023-03-02T00:00:00+00:00"),
        isToday = true,
        goToPreviousDate = {},
        goToNextDate = {},
        onDateChanged = { _ -> },
        logs = listOf(),
    )
}

@Preview
@Composable
fun HomeBodyWithLogsPreview() {
    HomeBody(
        date = OffsetDateTime.parse("2023-03-02T00:00:00+00:00"),
        isToday = false,
        goToPreviousDate = {},
        goToNextDate = {},
        onDateChanged = { _ -> },
        logs = listOf(
            FoodLogWithItems(
                log = FoodLog(1, OffsetDateTime.parse("2023-03-02T08:30:00+00:00")), items = listOf(
                    Item(1, "Banana"),
                    Item(2, "Oats"),
                    Item(3, "Yogurt"),
                )
            ), SymptomLogWithSymptoms(
                log = SymptomLog(1, OffsetDateTime.parse("2023-03-02T09:00:00+00:00")),
                items = listOf(
                    Symptom(1, "Bloating")
                )
            ), FoodLogWithItems(
                log = FoodLog(2, OffsetDateTime.parse("2023-03-02T13:15:00+00:00")), items = listOf(
                    Item(4, "Chicken"),
                    Item(5, "Rice"),
                    Item(6, "Peppers"),
                    Item(7, "Chorizo"),
                    Item(8, "Onion"),
                    Item(9, "Olive oil"),
                )
            ), MovementLog(
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
