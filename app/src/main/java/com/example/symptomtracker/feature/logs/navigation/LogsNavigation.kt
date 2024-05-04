package com.example.symptomtracker.feature.logs.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.symptomtracker.feature.logs.LogsRoute

const val LOGS_ROUTE = "logs"

fun NavController.navigateToLogs(navOptions: NavOptions) = navigate(route = LOGS_ROUTE, navOptions)

fun NavGraphBuilder.logsScreen(
    onFoodClick: (Long) -> Unit,
    onAddFoodClick: () -> Unit,
    onAddSymptomClick: () -> Unit,
    onAddMovementClick: () -> Unit,
) {
    composable(route = LOGS_ROUTE) {
        LogsRoute(
            onFoodClick = onFoodClick,
            onAddFoodClick = onAddFoodClick,
            onAddSymptomClick = onAddSymptomClick,
            onAddMovementClick = onAddMovementClick,
        )
    }
}
