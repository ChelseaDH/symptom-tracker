package com.example.symptomtracker.ui.food

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.symptomtracker.R
import com.example.symptomtracker.SymptomTrackerTopAppBar
import com.example.symptomtracker.data.food.FoodLog
import com.example.symptomtracker.data.food.FoodLogWithItems
import com.example.symptomtracker.data.food.Item
import com.example.symptomtracker.ui.AppViewModelProvider
import com.example.symptomtracker.ui.theme.SymptomTrackerTheme
import java.time.OffsetDateTime

@Composable
fun FoodLogListScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FoodLogListViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    Scaffold(
        topBar = {
            SymptomTrackerTopAppBar(
                title = stringResource(R.string.food_logs_title),
                canNavigateBack = true,
                navigateUp = navigateBack,
            )
        },
        modifier = modifier
    ) { innerPadding ->
        FoodLogList(
            foodLogs = viewModel.uiState.foodLogs,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun FoodLogList(foodLogs: List<FoodLogWithItems>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items = foodLogs) {
            FoodLogCard(foodLog = it)
        }
    }
}

@Composable
fun FoodLogCard(foodLog: FoodLogWithItems, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = foodLog.log.date.toString(),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding()
            )
            HorizontalDivider()
            ItemsList(items = foodLog.items)
        }
    }
}

@Composable
fun ItemsList(items: List<Item>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        items.forEach { item ->
            ListItem(
                headlineContent = { Text(text = item.name) }
            )
        }
    }
}

@Composable
@Preview(showSystemUi = true)
fun AddFoodScreenPreview() {
    SymptomTrackerTheme {
        FoodLogList(
            foodLogs = listOf(
                FoodLogWithItems(
                    log = FoodLog(1, OffsetDateTime.now()),
                    items = listOf(
                        Item(1, "Banana"),
                        Item(2, "Oats"),
                    )
                ),
                FoodLogWithItems(
                    log = FoodLog(2, OffsetDateTime.now()),
                    items = listOf(
                        Item(1, "Banana"),
                        Item(3, "Yoghurt"),
                    )
                )
            )
        )
    }
}
