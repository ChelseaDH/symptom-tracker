package com.example.symptomtracker.feature.movement.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.symptomtracker.feature.movement.AddMovementScreen
import com.example.symptomtracker.feature.movement.EditMovementScreen
import com.example.symptomtracker.feature.movement.ViewMovementRoute

const val MOVEMENT_LOG_ID = "movementLogId"

const val VIEW_MOVEMENT_ROUTE = "view_movement"
const val ADD_MOVEMENT_ROUTE = "add_movement"
const val EDIT_MOVEMENT_ROUTE = "edit_movement"

fun NavController.navigateToAddMovement() = navigate(route = ADD_MOVEMENT_ROUTE)

fun NavGraphBuilder.addMovementScreen(
    navigateBack: () -> Unit,
) {
    composable(route = ADD_MOVEMENT_ROUTE) {
        AddMovementScreen(
            navigateBack = navigateBack,
        )
    }
}

fun NavController.navigateToEditMovement(logId: Long) = navigate("$EDIT_MOVEMENT_ROUTE/$logId")

fun NavGraphBuilder.editMovementScreen(
    navigateBack: () -> Unit,
) {
    composable(
        route = "$EDIT_MOVEMENT_ROUTE/{$MOVEMENT_LOG_ID}",
        arguments = listOf(navArgument(MOVEMENT_LOG_ID) { type = NavType.LongType })
    ) {
        EditMovementScreen(
            navigateBack = navigateBack,
        )
    }
}

fun NavController.navigateToViewMovement(movementLogId: Long) =
    navigate("${VIEW_MOVEMENT_ROUTE}/$movementLogId")

fun NavGraphBuilder.viewMovementScreen(
    navigateBack: () -> Unit,
    navigateToEdit: (logId: Long) -> Unit
) {
    composable(
        route = "${VIEW_MOVEMENT_ROUTE}/{$MOVEMENT_LOG_ID}",
        arguments = listOf(navArgument(MOVEMENT_LOG_ID) { type = NavType.LongType })
    ) { backStackEntry ->
        ViewMovementRoute(
            navigateBack = navigateBack,
            navigateToEdit = {
                backStackEntry.arguments?.let {
                    navigateToEdit(
                        it.getLong(
                            MOVEMENT_LOG_ID
                        )
                    )
                }
            },
        )
    }
}
