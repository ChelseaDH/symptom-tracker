package com.example.symptomtracker.feature.drink.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.symptomtracker.core.domain.model.DrinkLog
import com.example.symptomtracker.feature.drink.AddDrinkScreen
import com.example.symptomtracker.feature.drink.EditDrinkScreen
import com.example.symptomtracker.feature.drink.ManageDrinkItemsRoute
import com.example.symptomtracker.feature.drink.ViewDrinkRoute
import com.example.symptomtracker.navigation.DATE_ARG
import com.example.symptomtracker.navigation.PREFILL_ITEMS
import com.example.symptomtracker.navigation.dateNavArgument
import com.example.symptomtracker.navigation.encodePrefillItems
import com.example.symptomtracker.navigation.formatDate
import com.example.symptomtracker.navigation.prefillNavArgument
import java.time.LocalDate

const val DRINK_LOG_ID = "drinkLogId"

const val ADD_DRINK_ROUTE = "add_drink"
const val EDIT_DRINK_ROUTE = "edit_drink"
const val VIEW_DRINK_ROUTE = "view_drink"
const val MANAGE_DRINK_ITEMS_ROUTE = "manage_drink_items"

fun NavController.navigateToAddDrink(prefillItems: List<String>? = null) {
    navigate(route = "$ADD_DRINK_ROUTE?$PREFILL_ITEMS=${encodePrefillItems(prefillItems)}")
}

fun NavController.navigateToAddDrink(drinkLog: DrinkLog) {
    val prefillItemsArg = encodePrefillItems(drinkLog.items.map { it.name })
    navigate(route = "$ADD_DRINK_ROUTE?$PREFILL_ITEMS=$prefillItemsArg")
}

fun NavController.navigateToAddDrink(date: LocalDate) {
    navigate(route = "$ADD_DRINK_ROUTE?$DATE_ARG=${formatDate(date)}")
}

fun NavGraphBuilder.addDrinkScreen(
    navigateBack: () -> Unit,
) {
    composable(
        route = "$ADD_DRINK_ROUTE?$PREFILL_ITEMS={$PREFILL_ITEMS}&$DATE_ARG={$DATE_ARG}",
        arguments = listOf(
            prefillNavArgument(),
            dateNavArgument(),
        )
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

fun NavController.navigateToManageDrinkItems() = navigate(route = MANAGE_DRINK_ITEMS_ROUTE)

fun NavGraphBuilder.manageDrinkItemsScreen(navigateBack: () -> Unit) {
    composable(route = MANAGE_DRINK_ITEMS_ROUTE) { ManageDrinkItemsRoute(navigateBack) }
}
