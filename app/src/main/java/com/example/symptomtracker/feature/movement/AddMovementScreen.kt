package com.example.symptomtracker.feature.movement

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.symptomtracker.R

@Composable
fun AddMovementScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddMovementViewModel = hiltViewModel(),
) {
    MovementEntryScreen(
        navigateBack = navigateBack,
        titleId = R.string.log_movement_title,
        viewModel = viewModel,
        modifier = modifier,
    )
}
