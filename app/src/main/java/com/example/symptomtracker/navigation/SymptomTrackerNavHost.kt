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

@Composable
fun SymptomTrackerNavHost(
    navController: NavHostController,
) {
    NavHost(navController = navController, startDestination = HOME_ROUTE) {
        homeScreen(
            navigateToAddFood = navController::navigateToFoodEntry,
            navigateToAddSymptom = navController::navigateToSymptomEntry,
            navigateToAddMovement = navController::navigateToMovementEntry,
        )
        logsScreen(
            onAddFoodClick = navController::navigateToFoodEntry,
            onAddSymptomClick = navController::navigateToSymptomEntry,
            onAddMovementClick = navController::navigateToMovementEntry,
        )
        foodEntryScreen(navigateBack = navController::navigateUp)
        movementEntryScreen(navigateBack = navController::navigateUp)
        symptomEntryScreen(navigateBack = navController::navigateUp)
    }
}
