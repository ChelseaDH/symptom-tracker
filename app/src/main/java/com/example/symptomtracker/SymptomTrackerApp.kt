package com.example.symptomtracker

import AddFoodScreen
import AddMovementScreen
import AddSymptomScreen
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.symptomtracker.ui.food.FoodLogListScreen
import com.example.symptomtracker.ui.home.HomeScreen
import com.example.symptomtracker.ui.movement.MovementLogListScreen
import com.example.symptomtracker.ui.symptom.SymptomLogListScreen

enum class Route {
    HOME, ADD_FOOD, ADD_SYMPTOM, ADD_MOVEMENT, VIEW_FOOD_LOGS, VIEW_SYMPTOM_LOGS, VIEW_MOVEMENT_LOGS
}

@Composable
fun SymptomTrackerApp() {
    val navController = rememberNavController()
    SymptomTrackerNavHost(navController = navController)
}

@Composable
fun SymptomTrackerNavHost(
    navController: NavHostController,
) {
    NavHost(navController = navController, startDestination = Route.HOME.name) {
        composable(route = Route.HOME.name) {
            HomeScreen(
                navigateToAddFood = { navController.navigate(Route.ADD_FOOD.name) },
                navigateToAddSymptom = { navController.navigate(Route.ADD_SYMPTOM.name) },
                navigateToAddMovement = { navController.navigate(Route.ADD_MOVEMENT.name) },
                navigateToViewFoodLogs = { navController.navigate(Route.VIEW_FOOD_LOGS.name) },
                navigateToViewSymptomLogs = { navController.navigate(Route.VIEW_SYMPTOM_LOGS.name) },
                navigateToViewMovementLogs = { navController.navigate(Route.VIEW_MOVEMENT_LOGS.name) },
            )
        }
        composable(route = Route.ADD_FOOD.name) {
            AddFoodScreen(navigateBack = { navController.navigateUp() })
        }
        composable(route = Route.ADD_SYMPTOM.name) {
            AddSymptomScreen(navigateBack = { navController.navigateUp() })
        }
        composable(route = Route.ADD_MOVEMENT.name) {
            AddMovementScreen(navigateBack = { navController.navigateUp() })
        }
        composable(route = Route.VIEW_FOOD_LOGS.name) {
            FoodLogListScreen(navigateBack = { navController.navigateUp() })
        }
        composable(route = Route.VIEW_SYMPTOM_LOGS.name) {
            SymptomLogListScreen(navigateBack = { navController.navigateUp() })
        }
        composable(route = Route.VIEW_MOVEMENT_LOGS.name) {
            MovementLogListScreen(navigateBack = { navController.navigateUp() })
        }
    }
}

/**
 * App bar to display title, optional actions, and conditionally display the back navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymptomTrackerTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit = {},
    actions: @Composable (RowScope.() -> Unit) = {},
) {
    if (canNavigateBack) {
        CenterAlignedTopAppBar(title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        }, navigationIcon = {
            IconButton(onClick = { navigateUp() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_button_cd)
                )
            }
        },
            actions = actions,
            modifier = modifier
        )
    } else {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            actions = actions,
            modifier = modifier
        )
    }
}