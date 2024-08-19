package com.example.symptomtracker.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.symptomtracker.feature.settings.DatabaseSettingsRoute
import com.example.symptomtracker.feature.settings.SettingsScreen

const val SETTINGS_ROUTE = "settings"
const val DATABASE_SETTINGS_ROUTE = "database_settings"

fun NavController.navigateToSettings(navOptions: NavOptions) =
    navigate(route = SETTINGS_ROUTE, navOptions)

fun NavGraphBuilder.settingsScreen(
    navigateToManageFoodItems: () -> Unit,
    navigateToDatabaseSettings: () -> Unit,
) {
    composable(route = SETTINGS_ROUTE) {
        SettingsScreen(
            navigateToManageFoodItems = navigateToManageFoodItems,
            navigateToDatabaseSettings = navigateToDatabaseSettings,
        )
    }
}

fun NavController.navigateToDatabaseSettings() =
    navigate(route = DATABASE_SETTINGS_ROUTE)

fun NavGraphBuilder.databaseSettingsScreen(
    navigateBack: () -> Unit,
) {
    composable(route = DATABASE_SETTINGS_ROUTE) {
        DatabaseSettingsRoute(navigateBack = navigateBack)
    }
}
