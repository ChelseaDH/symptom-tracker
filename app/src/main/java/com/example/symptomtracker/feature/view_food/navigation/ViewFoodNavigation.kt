package com.example.symptomtracker.feature.view_food.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.symptomtracker.feature.view_food.ViewFoodRoute

const val FOOD_LOG_ID = "foodLogId"
const val VIEW_FOOD_ROUTE = "food_log"

fun NavController.navigateToViewFood(foodLogId: Long) = navigate("$VIEW_FOOD_ROUTE/$foodLogId")

fun NavGraphBuilder.viewFoodScreen(navigateBack: () -> Unit) {
    composable(
        route = "$VIEW_FOOD_ROUTE/{$FOOD_LOG_ID}",
        arguments = listOf(navArgument(FOOD_LOG_ID) { type = NavType.LongType })
    ) {
        ViewFoodRoute(navigateBack = navigateBack)
    }
}
