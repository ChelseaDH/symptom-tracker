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
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.symptomtracker.R
import com.example.symptomtracker.core.designsystem.component.DatePickerModal
import com.example.symptomtracker.core.designsystem.component.FilledTonalButtonWithIcon
import com.example.symptomtracker.core.designsystem.icon.MealieIcon
import com.example.symptomtracker.core.domain.model.FoodLog
import com.example.symptomtracker.core.domain.model.Log
import com.example.symptomtracker.core.domain.model.MovementLog
import com.example.symptomtracker.core.domain.model.SymptomLog
import com.example.symptomtracker.core.domain.model.getDisplayName
import com.example.symptomtracker.core.domain.model.getDisplayString
import com.example.symptomtracker.core.ui.LogItemCard
import com.example.symptomtracker.core.ui.LogsPreviewParameterProvider
import com.example.symptomtracker.core.ui.NoLogsFoundCard
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToAddFood: () -> Unit,
    navigateToAddSymptom: () -> Unit,
    navigateToAddMovement: () -> Unit,
    onFoodClick: (Long) -> Unit,
    onSymptomClick: (Long) -> Unit,
    onMovementClick: (Long) -> Unit,
    onMealieImport: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel = hiltViewModel(),
) {
    val mealieIntegrationEnabled by viewModel.mealieIntegrationEnabled.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    fun onQuickAddNavigation() {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            viewModel.handleEvent(HomeScreenEvent.UpdateBottomSheetVisibility(false))
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.handleEvent(
                        HomeScreenEvent.UpdateBottomSheetVisibility(
                            true
                        )
                    )
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.action_add)
                )
            }
        }, modifier = modifier
    ) { innerPadding ->
        HomeBody(
            date = viewModel.uiState.date,
            isToday = viewModel.uiState.isToday,
            goToPreviousDate = { viewModel.handleEvent(HomeScreenEvent.GoToPreviousDay) },
            goToNextDate = { viewModel.handleEvent(HomeScreenEvent.GoToNextDay) },
            onDateChanged = { viewModel.handleEvent(HomeScreenEvent.UpdateDate(it)) },
            onFoodClick = onFoodClick,
            onSymptomClick = onSymptomClick,
            onMovementClick = onMovementClick,
            logs = viewModel.uiState.logs,
            modifier = Modifier.padding(innerPadding),
        )
        if (viewModel.uiState.showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    viewModel.handleEvent(
                        HomeScreenEvent.UpdateBottomSheetVisibility(
                            false
                        )
                    )
                },
                sheetState = sheetState,
            ) {
                AddLogsBottomSheetContent(mealieIntegrationEnabled = mealieIntegrationEnabled,
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
                    },
                    onImportFoodClick = {
                        onQuickAddNavigation()
                        onMealieImport()
                    })
            }
        }
    }
}

@Composable
fun HomeBody(
    date: LocalDate,
    isToday: Boolean,
    goToPreviousDate: () -> Unit,
    goToNextDate: () -> Unit,
    onDateChanged: (LocalDate) -> Unit,
    onFoodClick: (Long) -> Unit,
    onSymptomClick: (Long) -> Unit,
    onMovementClick: (Long) -> Unit,
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
            onFoodClick = onFoodClick,
            onSymptomClick = onSymptomClick,
            onMovementClick = onMovementClick,
        )
    }
}

@Composable
fun DateToggleRow(
    date: LocalDate,
    isToday: Boolean,
    goToPreviousDate: () -> Unit,
    goToNextDate: () -> Unit,
    onDateChanged: (LocalDate) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    if (showDatePicker) {
        DatePickerModal(
            date = date,
            onDismissRequest = { showDatePicker = false },
            onDateSelected = onDateChanged,
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
    onFoodClick: (Long) -> Unit,
    onSymptomClick: (Long) -> Unit,
    onMovementClick: (Long) -> Unit,
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
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(items = logs) { log ->
                        when (log) {
                            is FoodLog -> LogItemCard(
                                icon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.outline_nutrition_24),
                                        contentDescription = stringResource(R.string.add_food_text)
                                    )
                                },
                                title = stringResource(R.string.add_food_text),
                                date = log.date,
                                dateTimeFormatter = DateTimeFormatter.ofPattern(stringResource(R.string.datetime_format_hh_mm)),
                                supportingText = log.items.joinToString { it.name },
                                onClick = { onFoodClick(log.id) },
                            )

                            is SymptomLog -> LogItemCard(
                                icon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.outline_symptoms_24),
                                        contentDescription = stringResource(R.string.add_symptom_text)
                                    )
                                },
                                title = stringResource(R.string.add_symptom_text),
                                date = log.date,
                                dateTimeFormatter = DateTimeFormatter.ofPattern(stringResource(R.string.datetime_format_hh_mm)),
                                supportingText = log.items.joinToString { it.getDisplayString() },
                                onClick = { onSymptomClick(log.id) },
                            )

                            is MovementLog -> LogItemCard(icon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.outline_gastroenterology_24),
                                    contentDescription = stringResource(R.string.add_movement_text)
                                )
                            },
                                title = stringResource(R.string.add_movement_text),
                                date = log.date,
                                dateTimeFormatter = DateTimeFormatter.ofPattern(stringResource(R.string.datetime_format_hh_mm)),
                                supportingText = log.stoolType.getDisplayName(),
                                onClick = { onMovementClick(log.id) })
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun AddLogsBottomSheetContent(
    mealieIntegrationEnabled: Boolean,
    onAddFoodClick: () -> Unit,
    onAddSymptomClick: () -> Unit,
    onAddMovementClick: () -> Unit,
    onImportFoodClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.home_manual_add_title),
            style = MaterialTheme.typography.titleMedium
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            maxItemsInEachRow = 3,
        ) {
            FilledTonalButtonWithIcon(
                textId = R.string.add_food_text,
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_nutrition_24),
                        contentDescription = stringResource(R.string.add_food_cd)
                    )
                },
                onClick = onAddFoodClick,
            )
            FilledTonalButtonWithIcon(
                textId = R.string.add_symptom_text,
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_symptoms_24),
                        contentDescription = stringResource(R.string.add_symptom_cd)
                    )
                },
                onClick = onAddSymptomClick,
            )
            FilledTonalButtonWithIcon(
                textId = R.string.add_movement_text,
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_gastroenterology_24),
                        contentDescription = stringResource(R.string.add_movement_cd)
                    )
                },
                onClick = onAddMovementClick,
            )
        }

        if (mealieIntegrationEnabled) {
            Text(
                text = "Import", style = MaterialTheme.typography.titleMedium
            )
            FilledTonalButtonWithIcon(
                textId = R.string.mealie_import_title,
                icon = { MealieIcon() },
                onClick = onImportFoodClick,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeBodyWithNoLogsPreview() {
    HomeBody(
        date = LocalDate.now(),
        isToday = true,
        goToPreviousDate = {},
        goToNextDate = {},
        onDateChanged = { _ -> },
        onFoodClick = {},
        onSymptomClick = {},
        onMovementClick = {},
        logs = listOf(),
    )
}

@Preview(showBackground = true)
@Composable
fun HomeBodyWithLogsPreview(@PreviewParameter(LogsPreviewParameterProvider::class) logs: List<Log>) {
    HomeBody(
        date = LocalDate.now(),
        isToday = false,
        goToPreviousDate = {},
        goToNextDate = {},
        onDateChanged = { _ -> },
        onFoodClick = {},
        onSymptomClick = {},
        onMovementClick = {},
        logs = logs,
    )
}

@Preview(showBackground = true)
@Composable
fun AddLogsBottomSheetContentPreviewWithIntegration() {
    AddLogsBottomSheetContent(
        mealieIntegrationEnabled = true,
        onAddFoodClick = {},
        onImportFoodClick = {},
        onAddSymptomClick = {},
        onAddMovementClick = {},
    )
}

@Preview(showBackground = true)
@Composable
fun AddLogsBottomSheetContentPreviewWithoutIntegration() {
    AddLogsBottomSheetContent(
        mealieIntegrationEnabled = false,
        onAddFoodClick = {},
        onImportFoodClick = {},
        onAddSymptomClick = {},
        onAddMovementClick = {},
    )
}
