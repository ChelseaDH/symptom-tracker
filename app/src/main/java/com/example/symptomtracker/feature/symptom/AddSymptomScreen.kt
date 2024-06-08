package com.example.symptomtracker.feature.symptom

import SymptomEntryScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.symptomtracker.R

@Composable
fun AddSymptomScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddSymptomViewModel = hiltViewModel(),
) {
    SymptomEntryScreen(
        navigateBack = navigateBack,
        titleId = R.string.log_symptom_title,
        viewModel = viewModel,
        modifier = modifier,
    )
}
