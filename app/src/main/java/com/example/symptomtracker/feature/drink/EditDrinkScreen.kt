package com.example.symptomtracker.feature.drink

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.symptomtracker.R

@Composable
fun EditDrinkScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditDrinkViewModel = hiltViewModel(),
) {
    DrinkEntryScreen(
        navigateBack = navigateBack,
        titleId = R.string.edit_drink_title,
        viewModel = viewModel,
        modifier = modifier,
    )
}
