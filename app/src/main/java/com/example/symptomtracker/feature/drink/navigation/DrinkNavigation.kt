package com.example.symptomtracker.feature.drink.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.symptomtracker.feature.drink.AddDrinkScreen

const val PREFILL_ITEMS = "prefillItems"

const val ADD_DRINK_ROUTE = "add_drink"

fun NavController.navigateToAddDrink(prefillItems: List<String>? = null) {
    val prefillItemsArg = prefillItems?.joinToString(separator = ",", prefix = "[", postfix = "]")
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
