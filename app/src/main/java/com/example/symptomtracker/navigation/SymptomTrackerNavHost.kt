package com.example.symptomtracker.navigation

import AddFoodScreen
import AddMovementScreen
import AddSymptomScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.symptomtracker.feature.home.HomeScreen
import com.example.symptomtracker.feature.logs.navigation.logsScreen
import com.example.symptomtracker.ui.Route

@Composable
fun SymptomTrackerNavHost(
    navController: NavHostController,
) {
    NavHost(navController = navController, startDestination = Route.HOME.name) {
        composable(route = Route.HOME.name) {
            HomeScreen(
                navigateToAddFood = { navController.navigate(Route.ADD_FOOD.name) },
                navigateToAddSymptom = { navController.navigate(Route.ADD_SYMPTOM.name) },
                navigateToAddMovement = { navController.navigate(Route.ADD_MOVEMENT.name) },
            )
        }
        composable(route = Route.ADD_FOOD.name) {
            AddFoodScreen(navigateBack = { navController.navigateUp() })
        }
        composable(route = Route.ADD_SYMPTOM.name) {
            AddSymptomScreen(navigateBack = { navController.navigateUp() })
        }
        composable(route = Route.ADD_MOVEMENT.name) {
            AddMovementScreen(navigateBack = { navController.navigateUp() })
        }
        logsScreen(
            onAddFoodClick = { navController.navigate(Route.ADD_FOOD.name) },
            onAddSymptomClick = { navController.navigate(Route.ADD_SYMPTOM.name) },
            onAddMovementClick = { navController.navigate(Route.ADD_MOVEMENT.name) },
        )
    }
}
