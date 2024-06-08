package com.example.symptomtracker.feature.symptom

import SymptomEntryScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.symptomtracker.R

@Composable
fun EditSymptomScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditSymptomViewModel = hiltViewModel(),
) {
    SymptomEntryScreen(
        navigateBack = navigateBack,
        titleId = R.string.edit_symptom_title,
        viewModel = viewModel,
        modifier = modifier,
    )
}
