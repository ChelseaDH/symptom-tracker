package com.example.symptomtracker.feature.view_movement.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.symptomtracker.feature.view_movement.ViewMovementRoute

const val MOVEMENT_LOG_ID = "movementLogId"
const val VIEW_MOVEMENT_ROUTE = "movement_log"

fun NavController.navigateToViewMovement(movementLogId: Long) =
    navigate("$VIEW_MOVEMENT_ROUTE/$movementLogId")

fun NavGraphBuilder.viewMovementScreen(navigateBack: () -> Unit) {
    composable(
        route = "$VIEW_MOVEMENT_ROUTE/{$MOVEMENT_LOG_ID}",
        arguments = listOf(navArgument(MOVEMENT_LOG_ID) { type = NavType.LongType })
    ) {
        ViewMovementRoute(navigateBack = navigateBack)
    }
}
