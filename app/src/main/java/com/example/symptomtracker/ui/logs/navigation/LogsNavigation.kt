package com.example.symptomtracker.ui.logs.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.symptomtracker.ui.logs.LogsRoute

const val LOGS_ROUTE = "logs"

fun NavController.navigateToLogs(navOptions: NavOptions) = navigate(route = LOGS_ROUTE, navOptions)

fun NavGraphBuilder.logsScreen() {
    composable(route = LOGS_ROUTE) {
        LogsRoute()
    }
}
