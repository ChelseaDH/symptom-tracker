package com.example.symptomtracker.feature.logs

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.symptomtracker.R
import com.example.symptomtracker.core.database.model.FoodLogWithItems
import com.example.symptomtracker.core.model.Log
import com.example.symptomtracker.core.model.getDisplayName
import com.example.symptomtracker.core.ui.FoodLogsPreviewProvider
import com.example.symptomtracker.core.ui.LogItemCard
import java.time.format.DateTimeFormatter

@Composable
fun LogsRoute(
    onAddFoodClick: () -> Unit,
    onAddSymptomClick: () -> Unit,
    onAddMovementClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LogsViewModel = hiltViewModel(),
) {
    LogsScreen(
        tabs = viewModel.tabs,
        selectedTabIndex = viewModel.uiState.selectedTabIndex,
        tabState = viewModel.uiState.tabState,
        onSelectedTabChange = viewModel::updateSelectedTab,
        onLeftSwipe = viewModel::goToNextTab,
        onRightSwipe = viewModel::goToPreviousTab,
        onAddFoodClick = onAddFoodClick,
        onAddSymptomClick = onAddSymptomClick,
        onAddMovementClick = onAddMovementClick,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LogsScreen(
    tabs: List<String>,
    selectedTabIndex: Int,
    tabState: TabUiState,
    onSelectedTabChange: (Int) -> Unit,
    onLeftSwipe: () -> Unit,
    onRightSwipe: () -> Unit,
    onAddFoodClick: () -> Unit,
    onAddSymptomClick: () -> Unit,
    onAddMovementClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var swipeOffset by remember { mutableFloatStateOf(0f) }

    var fabOnClick: () -> Unit by remember { mutableStateOf({}) }
    var tabContent: LazyListScope.() -> Unit by remember { mutableStateOf({}) }

    when (tabState) {
        is TabUiState.FoodLogs -> {
            fabOnClick = onAddFoodClick
            tabContent = {
                items(items = tabState.logs) { foodLog ->
                    LogCard(log = foodLog, foodLog.items.joinToString { it.name })
                }
            }
        }

        is TabUiState.SymptomLogs -> {
            fabOnClick = onAddSymptomClick
            tabContent = {
                items(items = tabState.logs) { symptomLog ->
                    LogCard(log = symptomLog, symptomLog.items.joinToString { it.name })
                }
            }
        }

        is TabUiState.MovementLogs -> {
            fabOnClick = onAddMovementClick
            tabContent = {
                items(items = tabState.logs) {
                    LogCard(log = it, it.stoolType.getDisplayName())
                }
            }
        }

        else -> Unit
    }

    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = fabOnClick) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.add)
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
                        onClick = { onSelectedTabChange(index) },
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
                                    onLeftSwipe()
                                } else if (swipeOffset > 200) {
                                    onRightSwipe()
                                }
                            }) { _, dragAmount -> swipeOffset += dragAmount }
                    },
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                tabContent()
            }
        }
    }
}

@Composable
internal fun LogCard(log: Log, supportingText: String) {
    LogItemCard(
        title = log.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
        date = log.getDate(),
        dateTimeFormatter = DateTimeFormatter.ofPattern(stringResource(R.string.datetime_format_hh_mm)),
        supportingText = supportingText
    )
}

@Preview(showBackground = true)
@Composable
fun LogsScreenPreview(@PreviewParameter(FoodLogsPreviewProvider::class) foodLogs: List<FoodLogWithItems>) {
    LogsScreen(
        tabs = listOf("Food", "Symptom"),
        selectedTabIndex = 0,
        tabState = TabUiState.FoodLogs(logs = foodLogs),
        onSelectedTabChange = {},
        onLeftSwipe = {},
        onRightSwipe = {},
        onAddFoodClick = {},
        onAddSymptomClick = {},
        onAddMovementClick = {},
    )
}
