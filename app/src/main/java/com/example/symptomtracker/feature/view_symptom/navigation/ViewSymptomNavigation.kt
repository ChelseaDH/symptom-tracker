package com.example.symptomtracker.feature.view_symptom.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.symptomtracker.feature.view_symptom.ViewSymptomRoute

const val SYMPTOM_LOG_ID = "symptomLogId"
const val VIEW_SYMPTOM_ROUTE = "symptom_log"

fun NavController.navigateToViewSymptom(symptomLogId: Long) =
    navigate("$VIEW_SYMPTOM_ROUTE/$symptomLogId")

fun NavGraphBuilder.viewSymptomScreen(navigateBack: () -> Unit) {
    composable(
        route = "$VIEW_SYMPTOM_ROUTE/{$SYMPTOM_LOG_ID}",
        arguments = listOf(navArgument(SYMPTOM_LOG_ID) { type = NavType.LongType })
    ) {
        ViewSymptomRoute(navigateBack = navigateBack)
    }
}
