package com.example.symptomtracker.feature.logs

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.symptomtracker.R
import com.example.symptomtracker.core.designsystem.component.FloatingButtonMenu
import com.example.symptomtracker.core.designsystem.icon.AddIcon
import com.example.symptomtracker.core.designsystem.icon.MealieIcon
import com.example.symptomtracker.core.domain.model.FoodLog
import com.example.symptomtracker.core.domain.model.Log
import com.example.symptomtracker.core.domain.model.getDisplayName
import com.example.symptomtracker.core.domain.model.getDisplayString
import com.example.symptomtracker.core.ui.FoodLogsPreviewParameterProvider
import com.example.symptomtracker.core.ui.LogItemCard
import java.time.format.DateTimeFormatter

@Composable
fun LogsRoute(
    onFoodClick: (Long) -> Unit,
    onAddFoodClick: () -> Unit,
    onSymptomClick: (Long) -> Unit,
    onAddSymptomClick: () -> Unit,
    onMovementClick: (Long) -> Unit,
    onAddMovementClick: () -> Unit,
    onMealieImport: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LogsViewModel = hiltViewModel(),
) {
    val mealieIntegrationEnabled by viewModel.mealieIntegrationEnabled.collectAsState()

    LogsScreen(
        tabs = viewModel.tabs,
        selectedTabIndex = viewModel.uiState.selectedTabIndex,
        tabState = viewModel.uiState.tabState,
        mealieIntegrationEnabled = mealieIntegrationEnabled,
        eventSink = viewModel::handleEvent,
        onFoodClick = onFoodClick,
        onAddFoodClick = onAddFoodClick,
        onSymptomClick = onSymptomClick,
        onAddSymptomClick = onAddSymptomClick,
        onMovementClick = onMovementClick,
        onAddMovementClick = onAddMovementClick,
        onMealieImport = onMealieImport,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LogsScreen(
    tabs: List<String>,
    selectedTabIndex: Int,
    tabState: TabUiState,
    mealieIntegrationEnabled: Boolean,
    eventSink: (LogsViewEvent) -> Unit,
    onFoodClick: (Long) -> Unit,
    onAddFoodClick: () -> Unit,
    onSymptomClick: (Long) -> Unit,
    onAddSymptomClick: () -> Unit,
    onMovementClick: (Long) -> Unit,
    onAddMovementClick: () -> Unit,
    onMealieImport: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var swipeOffset by remember { mutableFloatStateOf(0f) }

    var fab: @Composable (() -> Unit) by remember { mutableStateOf({}) }
    var tabContent: LazyListScope.() -> Unit by remember { mutableStateOf({}) }

    when (tabState) {
        is TabUiState.FoodLogs -> {
            fab = {
                AddFoodButton(
                    mealieIntegrationEnabled = mealieIntegrationEnabled,
                    onAddFoodClick = { onAddFoodClick() },
                    onImportFoodClick = { onMealieImport() },
                )
            }
            tabContent = {
                items(items = tabState.logs) { foodLog ->
                    LogCard(
                        log = foodLog,
                        supportingText = foodLog.items.joinToString { it.name },
                        onClick = { onFoodClick(foodLog.id) }
                    )
                }
            }
        }

        is TabUiState.SymptomLogs -> {
            fab = {
                AddButton(onClick = onAddSymptomClick)
            }
            tabContent = {
                items(items = tabState.logs) { symptomLog ->
                    LogCard(
                        log = symptomLog,
                        supportingText = symptomLog.items.joinToString { it.getDisplayString() },
                        onClick = { onSymptomClick(symptomLog.id) }
                    )
                }
            }
        }

        is TabUiState.MovementLogs -> {
            fab = {
                AddButton(onClick = onAddMovementClick)
            }
            tabContent = {
                items(items = tabState.logs) { movementLog ->
                    LogCard(
                        log = movementLog,
                        supportingText = movementLog.stoolType.getDisplayName(),
                        onClick = { onMovementClick(movementLog.id) }
                    )
                }
            }
        }

        else -> Unit
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        floatingActionButton = { fab() },
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(innerPadding),
        ) {
            SecondaryTabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { eventSink(LogsViewEvent.UpdateSelectedTab(index)) },
                        text = { Text(text = title) },
                    )
                }
            }

            LazyColumn(
                modifier = modifier
                    .padding(16.dp)
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragStart = { swipeOffset = 0f },
                            onDragEnd = {
                                if (swipeOffset < -200) {
                                    eventSink(LogsViewEvent.GoToNextTab)
                                } else if (swipeOffset > 200) {
                                    eventSink(LogsViewEvent.GoToPreviousTab)
                                }
                            }) { _, dragAmount -> swipeOffset += dragAmount }
                    },
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                tabContent()
            }
        }
    }
}

@Composable
internal fun LogCard(log: Log, supportingText: String, onClick: () -> Unit = {}) {
    LogItemCard(
        title = log.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
        date = log.date,
        dateTimeFormatter = DateTimeFormatter.ofPattern(stringResource(R.string.datetime_format_hh_mm)),
        supportingText = supportingText,
        onClick = onClick,
    )
}

@Composable
internal fun AddButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        AddIcon(contentDescription = stringResource(R.string.action_add))
    }
}

@Composable
internal fun AddFoodButton(
    mealieIntegrationEnabled: Boolean,
    onAddFoodClick: () -> Unit,
    onImportFoodClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (mealieIntegrationEnabled) {
        AddFoodMenu(
            onAddFood = onAddFoodClick,
            onImportFoodFromMealie = onImportFoodClick,
            modifier = modifier,
        )
    } else {
        AddButton(
            onClick = onAddFoodClick,
            modifier = modifier,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun AddFoodMenu(
    onAddFood: () -> Unit,
    onImportFoodFromMealie: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    FloatingButtonMenu(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        FloatingActionButtonMenuItem(
            onClick = onAddFood,
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.outline_nutrition_24),
                    contentDescription = stringResource(R.string.add_food_cd)
                )
            },
            text = { Text(text = stringResource(R.string.add_food_manually)) },
        )
        FloatingActionButtonMenuItem(
            onClick = onImportFoodFromMealie,
            icon = { MealieIcon() },
            text = { Text(text = stringResource(R.string.mealie_import_action)) },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LogsScreenPreview(@PreviewParameter(FoodLogsPreviewParameterProvider::class) foodLogs: List<FoodLog>) {
    LogsScreen(
        tabs = listOf("Food", "Symptom"),
        selectedTabIndex = 0,
        tabState = TabUiState.FoodLogs(logs = foodLogs),
        mealieIntegrationEnabled = true,
        eventSink = {},
        onFoodClick = {},
        onAddFoodClick = {},
        onSymptomClick = {},
        onAddSymptomClick = {},
        onMovementClick = {},
        onAddMovementClick = {},
        onMealieImport = {},
    )
}
