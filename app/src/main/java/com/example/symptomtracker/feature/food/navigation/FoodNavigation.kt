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
import com.example.symptomtracker.navigation.DATE_ARG
import com.example.symptomtracker.navigation.PREFILL_ITEMS
import com.example.symptomtracker.navigation.dateNavArgument
import com.example.symptomtracker.navigation.encodePrefillItems
import com.example.symptomtracker.navigation.formatDate
import com.example.symptomtracker.navigation.prefillNavArgument
import java.time.LocalDate

const val FOOD_LOG_ID = "foodLogId"

const val ADD_FOOD_ROUTE = "add_food"
const val EDIT_FOOD_ROUTE = "edit_food"
const val VIEW_FOOD_ROUTE = "view_food"
const val MANAGE_ITEMS_ROUTE = "manage_items"

fun NavController.navigateToAddFood(prefillItems: List<String>? = null, date: LocalDate? = null) {
    navigate(
        route = "$ADD_FOOD_ROUTE?$PREFILL_ITEMS=${encodePrefillItems(prefillItems)}&$DATE_ARG=${
            formatDate(
                date
            )
        }"
    ) {
        // The Mealie import screen is an intermediate page which we don't want to navigate back to
        popUpTo(MEALIE_IMPORT_ROUTE) {
            inclusive = true
        }
    }
}

fun NavController.navigateToAddFood(foodLog: FoodLog) {
    val prefillItemsArg = encodePrefillItems(foodLog.items.map { it.name })
    navigate(route = "$ADD_FOOD_ROUTE?$PREFILL_ITEMS=$prefillItemsArg")
}

fun NavController.navigateToAddFood(date: LocalDate) =
    navigate(route = "$ADD_FOOD_ROUTE?$DATE_ARG=${formatDate(date)}")

fun NavGraphBuilder.addFoodScreen(
    navigateBack: () -> Unit,
) {
    composable(
        route = "$ADD_FOOD_ROUTE?$PREFILL_ITEMS={$PREFILL_ITEMS}&$DATE_ARG={$DATE_ARG}",
        arguments = listOf(
            prefillNavArgument(),
            dateNavArgument(),
        )
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
