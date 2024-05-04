package com.example.symptomtracker.feature.view_food

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.symptomtracker.R
import com.example.symptomtracker.core.database.model.FoodLogWithItems
import com.example.symptomtracker.core.ui.FoodLogPreviewParameterProvider
import com.example.symptomtracker.core.ui.LogDateTime
import com.example.symptomtracker.ui.SymptomTrackerTopAppBar

@Composable
fun ViewFoodRoute(
    navigateBack: () -> Unit,
    viewModel: ViewFoodViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            SymptomTrackerTopAppBar(
                title = "",
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
    ) { innerPadding ->
        ViewFoodScreen(
            modifier = Modifier.padding(innerPadding),
            state = viewModel.uiState,
        )
    }
}

@Composable
internal fun ViewFoodScreen(
    modifier: Modifier = Modifier,
    state: ViewFoodUiState = ViewFoodUiState.Loading
) {
    Column(modifier = modifier.padding(8.dp)) {
        when (state) {
            ViewFoodUiState.Loading -> Unit

            is ViewFoodUiState.FoodLog -> {
                ViewFoodBody(
                    foodLog = state.foodLog,
                )
            }
        }
    }
}

@Composable
internal fun ViewFoodBody(
    foodLog: FoodLogWithItems,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.add_food_text),
                style = MaterialTheme.typography.headlineMedium,
            )
            LogDateTime(log = foodLog)
        }
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            foodLog.items.forEach { item ->
                ListItem(
                    headlineContent = { Text(text = item.name) }
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ViewFoodContentPreview(
    @PreviewParameter(
        FoodLogPreviewParameterProvider::class,
        limit = 1
    ) foodLog: FoodLogWithItems
) {
    ViewFoodBody(foodLog = foodLog)
}
