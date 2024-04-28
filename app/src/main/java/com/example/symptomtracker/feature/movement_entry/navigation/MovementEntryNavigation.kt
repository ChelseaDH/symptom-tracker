package com.example.symptomtracker.feature.movement_entry.navigation

import MovementEntryScreen
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val MOVEMENT_ENTRY_ROUTE = "movement_entry"

fun NavController.navigateToMovementEntry() = navigate(route = MOVEMENT_ENTRY_ROUTE)

fun NavGraphBuilder.movementEntryScreen(
    navigateBack: () -> Unit,
) {
    composable(route = MOVEMENT_ENTRY_ROUTE) {
        MovementEntryScreen(
            navigateBack = navigateBack,
        )
    }
}
