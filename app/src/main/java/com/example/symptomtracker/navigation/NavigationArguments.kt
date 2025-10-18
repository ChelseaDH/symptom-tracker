package com.example.symptomtracker.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument

const val DATE_ARG = "dateArg"

fun dateNavArgument() = navArgument(DATE_ARG) {
    type = NavType.StringType
    defaultValue = null
    nullable = true
}

const val PREFILL_ITEMS = "prefillItems"

fun prefillNavArgument() = navArgument(PREFILL_ITEMS) {
    type = NavType.StringType
    defaultValue = null
    nullable = true
}
