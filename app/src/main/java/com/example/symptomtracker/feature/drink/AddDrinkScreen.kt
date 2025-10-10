package com.example.symptomtracker.feature.drink

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.symptomtracker.R

@Composable
fun AddDrinkScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddDrinkViewModel = hiltViewModel(),
) {
    DrinkEntryScreen(
        navigateBack = navigateBack,
        titleId = R.string.log_drink_title,
        viewModel = viewModel,
        modifier = modifier,
    )
}
