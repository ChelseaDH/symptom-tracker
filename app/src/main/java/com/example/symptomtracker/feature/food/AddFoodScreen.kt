package com.example.symptomtracker.feature.food

import FoodEntryScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.symptomtracker.R

@Composable
fun AddFoodScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddFoodViewModel = hiltViewModel(),
) {
    FoodEntryScreen(
        navigateBack = navigateBack,
        titleId = R.string.log_food_title,
        viewModel = viewModel,
        modifier = modifier,
    )
}
