package com.example.symptomtracker.feature.logs

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.symptomtracker.core.designsystem.SymptomTrackerTheme
import com.example.symptomtracker.core.designsystem.component.FilledTonalButtonWithIcon
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

    var fabOnClick: () -> Unit by remember { mutableStateOf({}) }
    var tabContent: LazyListScope.() -> Unit by remember { mutableStateOf({}) }

    var addFoodBottomSheetOpen by remember { mutableStateOf(false) }

    when (tabState) {
        is TabUiState.FoodLogs -> {
            fabOnClick = {
                if (mealieIntegrationEnabled) addFoodBottomSheetOpen = true else onAddFoodClick()
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
            fabOnClick = onAddSymptomClick
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
            fabOnClick = onAddMovementClick
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

    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = fabOnClick) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.action_add)
            )
        }
    }) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SecondaryTabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { eventSink(LogsViewEvent.UpdateSelectedTab(index)) },
                        text = { Text(text = title) }
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                tabContent()
            }
        }
    }

    if (addFoodBottomSheetOpen) {
        AddFoodBottomSheet(
            onAddFood = onAddFoodClick,
            onImportFoodFromMealie = onMealieImport,
            onDismissRequest = { addFoodBottomSheetOpen = false }
        )
    }
}

@Composable
internal fun LogCard(log: Log, supportingText: String, onClick: () -> Unit = {}) {
    LogItemCard(
        title = log.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
        date = log.date,
        dateTimeFormatter = DateTimeFormatter.ofPattern(stringResource(R.string.datetime_format_hh_mm)),
        supportingText = supportingText,
        onClick = onClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddFoodBottomSheet(
    onAddFood: () -> Unit,
    onImportFoodFromMealie: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    val onButtonPress: (() -> Unit) -> Unit = { onClick ->
        onDismissRequest()
        onClick()
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
    ) {
        AddFoodBottomSheetContent(
            onAddFood = { onButtonPress(onAddFood) },
            onImportFoodFromMealie = { onButtonPress(onImportFoodFromMealie) }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun AddFoodBottomSheetContent(
    onAddFood: () -> Unit,
    onImportFoodFromMealie: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.add_food_cd),
            style = MaterialTheme.typography.titleMedium
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                16.dp,
                alignment = Alignment.CenterHorizontally
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            maxItemsInEachRow = 2,
        ) {
            FilledTonalButtonWithIcon(
                textId = R.string.action_add_manually,
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_nutrition_24),
                        contentDescription = stringResource(R.string.add_food_manually)
                    )
                },
                onClick = onAddFood,
            )
            FilledTonalButtonWithIcon(
                textId = R.string.mealie_import_action_import_food_from_recipe,
                icon = { MealieIcon() },
                onClick = onImportFoodFromMealie,
            )
        }
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

@Preview(showBackground = true)
@Composable
fun AddFoodBottomSheetContentPreview() {
    SymptomTrackerTheme {
        AddFoodBottomSheetContent(
            onAddFood = {},
            onImportFoodFromMealie = {},
        )
    }
}
