package com.example.symptomtracker.feature.food_entry.navigation

import FoodEntryScreen
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val FOOD_ENTRY_ROUTE = "food_entry"

fun NavController.navigateToFoodEntry() = navigate(route = FOOD_ENTRY_ROUTE)

fun NavGraphBuilder.foodEntryScreen(
    navigateBack: () -> Unit,
) {
    composable(route = FOOD_ENTRY_ROUTE) {
        FoodEntryScreen(
            navigateBack = navigateBack,
        )
    }
}
