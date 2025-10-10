package com.example.symptomtracker.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.symptomtracker.feature.home.HomeScreen

const val HOME_ROUTE = "home"

fun NavController.navigateToHome(navOptions: NavOptions) = navigate(route = HOME_ROUTE, navOptions)

fun NavGraphBuilder.homeScreen(
    navigateToAddFood: () -> Unit,
    navigateToAddDrink: () -> Unit,
    navigateToAddSymptom: () -> Unit,
    navigateToAddMovement: () -> Unit,
    onFoodClick: (Long) -> Unit,
    onSymptomClick: (Long) -> Unit,
    onMovementClick: (Long) -> Unit,
    navigateToMealieImport: () -> Unit,
) {
    composable(route = HOME_ROUTE) {
        HomeScreen(
            navigateToAddFood = navigateToAddFood,
            navigateToAddDrink = navigateToAddDrink,
            navigateToAddSymptom = navigateToAddSymptom,
            navigateToAddMovement = navigateToAddMovement,
            onFoodClick = onFoodClick,
            onSymptomClick = onSymptomClick,
            onMovementClick = onMovementClick,
            onMealieImport = navigateToMealieImport,
        )
    }
}
