package com.example.symptomtracker.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.symptomtracker.core.designsystem.component.FloatingButtonMenu
import com.example.symptomtracker.core.designsystem.icon.MealieIcon
import com.example.symptomtracker.core.domain.model.DrinkLog
import com.example.symptomtracker.core.domain.model.FoodLog
import com.example.symptomtracker.core.domain.model.Log
import com.example.symptomtracker.core.domain.model.MovementLog
import com.example.symptomtracker.core.domain.model.SymptomLog
import com.example.symptomtracker.core.domain.model.getDisplayName
import com.example.symptomtracker.core.domain.model.getDisplayString
import com.example.symptomtracker.core.ui.LogItemCard
import com.example.symptomtracker.core.ui.LogsPreviewParameterProvider
import com.example.symptomtracker.core.ui.NoLogsFoundCard
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    navigateToAddFood: () -> Unit,
    navigateToAddDrink: () -> Unit,
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

    fun closeQuickAddMenu() {
        viewModel.handleEvent(HomeScreenEvent.UpdateQuickAddMenuVisibility(false))
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        floatingActionButton = {
            QuickAddMenu(
                expanded = viewModel.uiState.showQuickAddMenu,
                onExpandedChange = {
                    viewModel.handleEvent(
                        HomeScreenEvent.UpdateQuickAddMenuVisibility(
                            it
                        )
                    )
                },
                onAddFoodClick = {
                    closeQuickAddMenu()
                    navigateToAddFood()
                },
                onAddDrinkClick = {
                    closeQuickAddMenu()
                    navigateToAddDrink()
                },
                onAddSymptomClick = {
                    closeQuickAddMenu()
                    navigateToAddSymptom()
                },
                onAddMovementClick = {
                    closeQuickAddMenu()
                    navigateToAddMovement()
                },
                onImportFoodClick = {
                    closeQuickAddMenu()
                    onMealieImport()
                },
                mealieIntegrationEnabled = mealieIntegrationEnabled,
            )
        },
        modifier = modifier,
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

        if (viewModel.uiState.showQuickAddMenu) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { closeQuickAddMenu() })
            )
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
                painter = painterResource(R.drawable.outline_keyboard_arrow_left_24),
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
                    painter = painterResource(R.drawable.outline_keyboard_arrow_right_24),
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
        Box(
            modifier = Modifier
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

                            is MovementLog -> LogItemCard(
                                icon = {
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

                            is DrinkLog -> LogItemCard(
                                icon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.outline_glass_cup_24),
                                        contentDescription = stringResource(R.string.add_drink_text)
                                    )
                                },
                                title = stringResource(R.string.add_drink_text),
                                date = log.date,
                                dateTimeFormatter = DateTimeFormatter.ofPattern(stringResource(R.string.datetime_format_hh_mm)),
                                supportingText = log.items.joinToString { it.name },
                                onClick = {})
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalMaterial3ExpressiveApi
@Composable
fun QuickAddMenu(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    mealieIntegrationEnabled: Boolean,
    onAddFoodClick: () -> Unit,
    onAddDrinkClick: () -> Unit,
    onAddSymptomClick: () -> Unit,
    onAddMovementClick: () -> Unit,
    onImportFoodClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FloatingButtonMenu(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = modifier,
    ) {
        FloatingActionButtonMenuItem(
            onClick = onAddFoodClick,
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.outline_nutrition_24),
                    contentDescription = stringResource(R.string.add_food_cd)
                )
            },
            text = { Text(text = stringResource(R.string.add_food_text)) },
        )
        FloatingActionButtonMenuItem(
            onClick = onAddDrinkClick,
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.outline_glass_cup_24),
                    contentDescription = stringResource(R.string.add_drink_cd)
                )
            },
            text = { Text(text = stringResource(R.string.add_drink_text)) },
        )
        FloatingActionButtonMenuItem(
            onClick = onAddSymptomClick,
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.outline_symptoms_24),
                    contentDescription = stringResource(R.string.add_symptom_cd)
                )
            },
            text = { Text(text = stringResource(R.string.add_symptom_text)) },
        )
        FloatingActionButtonMenuItem(
            onClick = onAddMovementClick,
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.outline_gastroenterology_24),
                    contentDescription = stringResource(R.string.add_movement_cd)
                )
            },
            text = { Text(text = stringResource(R.string.add_movement_text)) },
        )
        if (mealieIntegrationEnabled) {
            FloatingActionButtonMenuItem(
                onClick = onImportFoodClick,
                icon = { MealieIcon() },
                text = { Text(text = stringResource(R.string.mealie_import_action)) },
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true)
@Composable
fun QuickAddMenuClosed() {
    QuickAddMenu(
        expanded = false,
        onExpandedChange = {},
        mealieIntegrationEnabled = true,
        onAddFoodClick = {},
        onAddDrinkClick = {},
        onImportFoodClick = {},
        onAddSymptomClick = {},
        onAddMovementClick = {},
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true)
@Composable
fun QuickAddMenuWithIntegration() {
    QuickAddMenu(
        expanded = true,
        onExpandedChange = {},
        mealieIntegrationEnabled = true,
        onAddFoodClick = {},
        onAddDrinkClick = {},
        onImportFoodClick = {},
        onAddSymptomClick = {},
        onAddMovementClick = {},
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true)
@Composable
fun QuickAddMenuWithoutIntegration() {
    QuickAddMenu(
        expanded = true,
        onExpandedChange = {},
        mealieIntegrationEnabled = false,
        onAddFoodClick = {},
        onAddDrinkClick = {},
        onImportFoodClick = {},
        onAddSymptomClick = {},
        onAddMovementClick = {},
    )
}
