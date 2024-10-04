package com.example.symptomtracker.feature.food.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.symptomtracker.core.domain.model.FoodLog
import com.example.symptomtracker.feature.food.AddFoodScreen
import com.example.symptomtracker.feature.food.EditFoodScreen
import com.example.symptomtracker.feature.food.ManageFoodItemsRoute
import com.example.symptomtracker.feature.food.ViewFoodRoute
import com.example.symptomtracker.feature.mealie.navigation.MEALIE_IMPORT_ROUTE

const val FOOD_LOG_ID = "foodLogId"
const val PREFILL_ITEMS = "prefillItems"

const val ADD_FOOD_ROUTE = "add_food"
const val EDIT_FOOD_ROUTE = "edit_food"
const val VIEW_FOOD_ROUTE = "view_food"
const val MANAGE_ITEMS_ROUTE = "manage_items"

fun NavController.navigateToAddFood(prefillItems: List<String>? = null) {
    val prefillItemsArg = prefillItems?.joinToString(separator = ",", prefix = "[", postfix = "]")
    navigate(route = "$ADD_FOOD_ROUTE?$PREFILL_ITEMS=$prefillItemsArg") {
        // The Mealie import screen is an intermediate page which we don't want to navigate back to
        popUpTo(MEALIE_IMPORT_ROUTE) {
            inclusive = true
        }
    }
}

fun NavController.navigateToAddFood(foodLog: FoodLog) {
    val prefillItemsArg =
        foodLog.items.joinToString(separator = ",", prefix = "[", postfix = "]") { it.name }
    navigate(route = "$ADD_FOOD_ROUTE?$PREFILL_ITEMS=$prefillItemsArg")
}

fun NavGraphBuilder.addFoodScreen(
    navigateBack: () -> Unit,
) {
    composable(
        route = "$ADD_FOOD_ROUTE?$PREFILL_ITEMS={prefillItems}",
        arguments = listOf(navArgument(PREFILL_ITEMS) {
            type = NavType.StringType
            defaultValue = null
            nullable = true
        })
    ) {
        AddFoodScreen(navigateBack = navigateBack)
    }
}

fun NavController.navigateToEditFood(logId: Long) = navigate(route = "$EDIT_FOOD_ROUTE/$logId")

fun NavGraphBuilder.editFoodScreen(
    navigateBack: () -> Unit,
) {
    composable(
        route = "$EDIT_FOOD_ROUTE/{$FOOD_LOG_ID}",
        arguments = listOf(navArgument(FOOD_LOG_ID) { type = NavType.LongType })
    ) {
        EditFoodScreen(navigateBack = navigateBack)
    }
}

fun NavController.navigateToViewFood(foodLogId: Long) = navigate("${VIEW_FOOD_ROUTE}/$foodLogId")

fun NavGraphBuilder.viewFoodScreen(
    navigateBack: () -> Unit,
    navigateToEdit: (logId: Long) -> Unit,
    navigateToCopy: (foodLog: FoodLog) -> Unit,
) {
    composable(
        route = "${VIEW_FOOD_ROUTE}/{${FOOD_LOG_ID}}",
        arguments = listOf(navArgument(FOOD_LOG_ID) { type = NavType.LongType })
    ) { backStackEntry ->
        ViewFoodRoute(
            navigateBack = navigateBack,
            navigateToEdit = { backStackEntry.arguments?.let { navigateToEdit(it.getLong(FOOD_LOG_ID)) } },
            navigateToCopy = navigateToCopy,
        )
    }
}

fun NavController.navigateToManageFoodItems() = navigate(route = MANAGE_ITEMS_ROUTE)

fun NavGraphBuilder.manageFoodItemsScreen(navigateBack: () -> Unit) {
    composable(route = MANAGE_ITEMS_ROUTE) { ManageFoodItemsRoute(navigateBack) }
}
