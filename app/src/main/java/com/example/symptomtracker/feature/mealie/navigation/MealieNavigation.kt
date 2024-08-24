package com.example.symptomtracker.feature.mealie.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.symptomtracker.feature.mealie.MealieImportRoute
import com.example.symptomtracker.feature.mealie.MealieSettingsRoute

const val MEALIE_IMPORT_ROUTE = "mealie_import"
const val MEALIE_SETTINGS_ROUTE = "mealie_settings"

fun NavController.navigateToMealieImport() = navigate(route = MEALIE_IMPORT_ROUTE)

fun NavGraphBuilder.mealieImportScreen(
    navigateBack: () -> Unit,
    navigateToAddFood: (List<String>) -> Unit,
) {
    composable(route = MEALIE_IMPORT_ROUTE) {
        MealieImportRoute(navigateBack = navigateBack, navigateToAddFood = navigateToAddFood)
    }
}

fun NavController.navigateToMealieSettings() = navigate(route = MEALIE_SETTINGS_ROUTE)

fun NavGraphBuilder.mealieSettingsScreen(
    navigateBack: () -> Unit,
) {
    composable(route = MEALIE_SETTINGS_ROUTE) {
        MealieSettingsRoute(navigateBack = navigateBack)
    }
}
