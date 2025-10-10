package com.example.symptomtracker.feature.drink.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.symptomtracker.core.domain.model.DrinkLog
import com.example.symptomtracker.feature.drink.AddDrinkScreen
import com.example.symptomtracker.feature.drink.EditDrinkScreen
import com.example.symptomtracker.feature.drink.ViewDrinkRoute

const val DRINK_LOG_ID = "drinkLogId"
const val PREFILL_ITEMS = "prefillItems"

const val ADD_DRINK_ROUTE = "add_drink"
const val EDIT_DRINK_ROUTE = "edit_drink"
const val VIEW_DRINK_ROUTE = "view_drink"

fun NavController.navigateToAddDrink(prefillItems: List<String>? = null) {
    val prefillItemsArg = prefillItems?.joinToString(separator = ",", prefix = "[", postfix = "]")
    navigate(route = "$ADD_DRINK_ROUTE?$PREFILL_ITEMS=$prefillItemsArg")
}

fun NavController.navigateToAddDrink(drinkLog: DrinkLog) {
    val prefillItemsArg =
        drinkLog.items.joinToString(separator = ",", prefix = "[", postfix = "]") { it.name }
    navigate(route = "$ADD_DRINK_ROUTE?$PREFILL_ITEMS=$prefillItemsArg")
}

fun NavGraphBuilder.addDrinkScreen(
    navigateBack: () -> Unit,
) {
    composable(
        route = "$ADD_DRINK_ROUTE?$PREFILL_ITEMS={prefillItems}",
        arguments = listOf(navArgument(PREFILL_ITEMS) {
            type = NavType.StringType
            defaultValue = null
            nullable = true
        })
    ) {
        AddDrinkScreen(navigateBack = navigateBack)
    }
}

fun NavController.navigateToEditDrink(logId: Long) = navigate(route = "$EDIT_DRINK_ROUTE/$logId")

fun NavGraphBuilder.editDrinkScreen(
    navigateBack: () -> Unit,
) {
    composable(
        route = "$EDIT_DRINK_ROUTE/{$DRINK_LOG_ID}",
        arguments = listOf(navArgument(DRINK_LOG_ID) { type = NavType.LongType })
    ) {
        EditDrinkScreen(navigateBack = navigateBack)
    }
}

fun NavController.navigateToViewDrink(drinkLogId: Long) =
    navigate("${VIEW_DRINK_ROUTE}/$drinkLogId")

fun NavGraphBuilder.viewDrinkScreen(
    navigateBack: () -> Unit,
    navigateToEdit: (logId: Long) -> Unit,
    navigateToCopy: (drinkLog: DrinkLog) -> Unit,
) {
    composable(
        route = "${VIEW_DRINK_ROUTE}/{${DRINK_LOG_ID}}",
        arguments = listOf(navArgument(DRINK_LOG_ID) { type = NavType.LongType })
    ) { backStackEntry ->
        ViewDrinkRoute(
            navigateBack = navigateBack,
            navigateToEdit = {
                backStackEntry.arguments?.let {
                    navigateToEdit(
                        it.getLong(
                            DRINK_LOG_ID
                        )
                    )
                }
            },
            navigateToCopy = navigateToCopy,
        )
    }
}
