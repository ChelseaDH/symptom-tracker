package com.example.symptomtracker.feature.symptom.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.symptomtracker.feature.symptom.AddSymptomScreen
import com.example.symptomtracker.feature.symptom.EditSymptomScreen
import com.example.symptomtracker.feature.symptom.ViewSymptomRoute
import com.example.symptomtracker.navigation.DATE_ARG
import com.example.symptomtracker.navigation.dateNavArgument
import com.example.symptomtracker.navigation.formatDate
import java.time.LocalDate

const val SYMPTOM_LOG_ID = "symptomLogId"

const val ADD_SYMPTOM_ROUTE = "add_symptom"
const val EDIT_SYMPTOM_ROUTE = "edit_symptom"
const val VIEW_SYMPTOM_ROUTE = "view_symptom"

fun NavController.navigateToAddSymptom() = navigate(route = ADD_SYMPTOM_ROUTE)

fun NavController.navigateToAddSymptom(date: LocalDate) =
    navigate(route = "$ADD_SYMPTOM_ROUTE?$DATE_ARG=${formatDate(date)}")

fun NavGraphBuilder.addSymptomScreen(
    navigateBack: () -> Unit,
) {
    composable(
        route = "$ADD_SYMPTOM_ROUTE?$DATE_ARG={$DATE_ARG}",
        arguments = listOf(dateNavArgument()),
    ) {
        AddSymptomScreen(
            navigateBack = navigateBack,
        )
    }
}

fun NavController.navigateToEditSymptom(logId: Long) =
    navigate(route = "$EDIT_SYMPTOM_ROUTE/$logId")

fun NavGraphBuilder.editSymptomScreen(
    navigateBack: () -> Unit,
) {
    composable(
        route = "$EDIT_SYMPTOM_ROUTE/{$SYMPTOM_LOG_ID}",
        arguments = listOf(navArgument(SYMPTOM_LOG_ID) { type = NavType.LongType })
    ) {
        EditSymptomScreen(navigateBack = navigateBack)
    }
}

fun NavController.navigateToViewSymptom(symptomLogId: Long) =
    navigate("${VIEW_SYMPTOM_ROUTE}/$symptomLogId")

fun NavGraphBuilder.viewSymptomScreen(
    navigateBack: () -> Unit, navigateToEdit: (logId: Long) -> Unit
) {
    composable(
        route = "${VIEW_SYMPTOM_ROUTE}/{${SYMPTOM_LOG_ID}}",
        arguments = listOf(navArgument(SYMPTOM_LOG_ID) { type = NavType.LongType })
    ) { backStackEntry ->
        ViewSymptomRoute(navigateBack = navigateBack, navigateToEdit = {
            backStackEntry.arguments?.let {
                navigateToEdit(
                    it.getLong(
                        SYMPTOM_LOG_ID
                    )
                )
            }
        })
    }
}
