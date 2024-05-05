package com.example.symptomtracker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.symptomtracker.feature.food_entry.navigation.foodEntryScreen
import com.example.symptomtracker.feature.food_entry.navigation.navigateToFoodEntry
import com.example.symptomtracker.feature.home.navigation.HOME_ROUTE
import com.example.symptomtracker.feature.home.navigation.homeScreen
import com.example.symptomtracker.feature.logs.navigation.logsScreen
import com.example.symptomtracker.feature.movement_entry.navigation.movementEntryScreen
import com.example.symptomtracker.feature.movement_entry.navigation.navigateToMovementEntry
import com.example.symptomtracker.feature.symptom_entry.navigation.navigateToSymptomEntry
import com.example.symptomtracker.feature.symptom_entry.navigation.symptomEntryScreen
import com.example.symptomtracker.feature.view_food.navigation.navigateToViewFood
import com.example.symptomtracker.feature.view_food.navigation.viewFoodScreen
import com.example.symptomtracker.feature.view_movement.navigation.navigateToViewMovement
import com.example.symptomtracker.feature.view_movement.navigation.viewMovementScreen
import com.example.symptomtracker.feature.view_symptom.navigation.navigateToViewSymptom
import com.example.symptomtracker.feature.view_symptom.navigation.viewSymptomScreen

@Composable
fun SymptomTrackerNavHost(
    navController: NavHostController,
) {
    NavHost(navController = navController, startDestination = HOME_ROUTE) {
        homeScreen(
            navigateToAddFood = navController::navigateToFoodEntry,
            navigateToAddSymptom = navController::navigateToSymptomEntry,
            navigateToAddMovement = navController::navigateToMovementEntry,
            onFoodClick = navController::navigateToViewFood,
            onSymptomClick = navController::navigateToViewSymptom,
            onMovementClick = navController::navigateToViewMovement,
        )
        logsScreen(
            onFoodClick = navController::navigateToViewFood,
            onAddFoodClick = navController::navigateToFoodEntry,
            onSymptomClick = navController::navigateToViewSymptom,
            onAddSymptomClick = navController::navigateToSymptomEntry,
            onMovementClick = navController::navigateToViewMovement,
            onAddMovementClick = navController::navigateToMovementEntry,
        )

        foodEntryScreen(navigateBack = navController::navigateUp)
        viewFoodScreen(navigateBack = navController::navigateUp)

        symptomEntryScreen(navigateBack = navController::navigateUp)
        viewSymptomScreen(navigateBack = navController::navigateUp)

        movementEntryScreen(navigateBack = navController::navigateUp)
        viewMovementScreen(navigateBack = navController::navigateUp)
    }
}
