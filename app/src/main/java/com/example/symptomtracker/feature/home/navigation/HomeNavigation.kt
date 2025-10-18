package com.example.symptomtracker.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.symptomtracker.feature.home.HomeScreen
import java.time.LocalDate

const val HOME_ROUTE = "home"

fun NavController.navigateToHome(navOptions: NavOptions) = navigate(route = HOME_ROUTE, navOptions)

fun NavGraphBuilder.homeScreen(
    navigateToAddFood: (date: LocalDate) -> Unit,
    navigateToAddDrink: (date: LocalDate) -> Unit,
    navigateToAddSymptom: (date: LocalDate) -> Unit,
    navigateToAddMovement: (date: LocalDate) -> Unit,
    onFoodClick: (Long) -> Unit,
    onDrinkClick: (Long) -> Unit,
    onSymptomClick: (Long) -> Unit,
    onMovementClick: (Long) -> Unit,
    navigateToMealieImport: (date: LocalDate) -> Unit,
) {
    composable(route = HOME_ROUTE) {
        HomeScreen(
            navigateToAddFood = navigateToAddFood,
            navigateToAddDrink = navigateToAddDrink,
            navigateToAddSymptom = navigateToAddSymptom,
            navigateToAddMovement = navigateToAddMovement,
            onFoodClick = onFoodClick,
            onDrinkClick = onDrinkClick,
            onSymptomClick = onSymptomClick,
            onMovementClick = onMovementClick,
            onMealieImport = navigateToMealieImport,
        )
    }
}
