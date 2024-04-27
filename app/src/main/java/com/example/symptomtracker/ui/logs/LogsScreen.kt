package com.example.symptomtracker.ui.logs

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.symptomtracker.R
import com.example.symptomtracker.data.Log
import com.example.symptomtracker.data.food.FoodLogWithItems
import com.example.symptomtracker.data.movement.getDisplayName
import com.example.symptomtracker.ui.AppViewModelProvider
import com.example.symptomtracker.ui.components.LogItemCard
import java.time.format.DateTimeFormatter

@Composable
fun LogsRoute(
    modifier: Modifier = Modifier,
    viewModel: LogsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    LogsScreen(
        tabs = viewModel.tabs,
        selectedTabIndex = viewModel.uiState.selectedTabIndex,
        tabState = viewModel.uiState.tabState,
        onSelectedTabChange = viewModel::updateSelectedTab,
        onLeftSwipe = viewModel::goToNextTab,
        onRightSwipe = viewModel::goToPreviousTab,
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
    modifier: Modifier = Modifier,
) {
    var swipeOffset by remember { mutableFloatStateOf(0f) }

    Column(
        modifier = modifier.fillMaxWidth(),
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
                    detectHorizontalDragGestures(onDragStart = { swipeOffset = 0f }, onDragEnd = {
                        if (swipeOffset < -200) {
                            onLeftSwipe()
                        } else if (swipeOffset > 200) {
                            onRightSwipe()
                        }
                    }) { _, dragAmount -> swipeOffset += dragAmount }
                },
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (tabState) {
                TabUiState.Loading -> Unit

                is TabUiState.FoodLogs -> items(items = tabState.logs) { foodLog ->
                    LogCard(log = foodLog, foodLog.items.joinToString { it.name })
                }

                is TabUiState.SymptomLogs -> items(items = tabState.logs) { symptomLog ->
                    LogCard(log = symptomLog, symptomLog.items.joinToString { it.name })
                }

                is TabUiState.MovementLogs -> items(items = tabState.logs) {
                    LogCard(log = it, it.stoolType.getDisplayName())
                }
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
    )
}
