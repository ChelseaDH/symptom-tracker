package com.example.symptomtracker.feature.mealie.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.symptomtracker.feature.mealie.MealieImportRoute
import com.example.symptomtracker.feature.mealie.MealieSettingsRoute
import com.example.symptomtracker.navigation.DATE_ARG
import com.example.symptomtracker.navigation.dateNavArgument
import com.example.symptomtracker.navigation.formatDate
import com.example.symptomtracker.navigation.prefillNavArgument
import java.time.LocalDate

const val MEALIE_IMPORT_ROUTE = "mealie_import"
const val MEALIE_SETTINGS_ROUTE = "mealie_settings"

fun NavController.navigateToMealieImport() = navigate(route = MEALIE_IMPORT_ROUTE)

fun NavController.navigateToMealieImport(date: LocalDate?) =
    navigate(route = "$MEALIE_IMPORT_ROUTE?$DATE_ARG=${formatDate(date)}")

fun NavGraphBuilder.mealieImportScreen(
    navigateBack: () -> Unit,
    navigateToAddFood: (List<String>, LocalDate?) -> Unit,
) {
    composable(
        route = "$MEALIE_IMPORT_ROUTE?$DATE_ARG={$DATE_ARG}",
        arguments = listOf(
            prefillNavArgument(),
            dateNavArgument(),
        ),
    ) {
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
