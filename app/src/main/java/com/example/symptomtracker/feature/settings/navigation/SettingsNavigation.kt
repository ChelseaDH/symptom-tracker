package com.example.symptomtracker.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.symptomtracker.feature.settings.SettingsScreen

const val SETTINGS_ROUTE = "settings"

fun NavController.navigateToSettings(navOptions: NavOptions) =
    navigate(route = SETTINGS_ROUTE, navOptions)

fun NavGraphBuilder.settingsScreen(
    navigateToManageFoodItems: () -> Unit,
    navigateToMealieSettings: () -> Unit,
) {
    composable(route = SETTINGS_ROUTE) {
        SettingsScreen(
            navigateToManageFoodItems = navigateToManageFoodItems,
            navigateToMealieSettings = navigateToMealieSettings,
        )
    }
}
