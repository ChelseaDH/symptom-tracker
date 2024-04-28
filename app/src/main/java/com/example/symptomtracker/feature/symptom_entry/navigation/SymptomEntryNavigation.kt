package com.example.symptomtracker.feature.symptom_entry.navigation

import SymptomEntryScreen
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val SYMPTOM_ENTRY_ROUTE = "symptom_entry"

fun NavController.navigateToSymptomEntry() = navigate(route = SYMPTOM_ENTRY_ROUTE)

fun NavGraphBuilder.symptomEntryScreen(
    navigateBack: () -> Unit,
) {
    composable(route = SYMPTOM_ENTRY_ROUTE) {
        SymptomEntryScreen(
            navigateBack = navigateBack,
        )
    }
}
