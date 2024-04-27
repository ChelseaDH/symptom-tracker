package com.example.symptomtracker

import AddFoodScreen
import AddMovementScreen
import AddSymptomScreen
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.symptomtracker.navigation.TopLevelDestination
import com.example.symptomtracker.ui.AppState
import com.example.symptomtracker.ui.home.HomeScreen
import com.example.symptomtracker.ui.logs.navigation.logsScreen

enum class Route {
    HOME, ADD_FOOD, ADD_SYMPTOM, ADD_MOVEMENT
}

@Composable
fun SymptomTrackerApp(appState: AppState) {
    val topLevelDestination = appState.currentTopLevelDestination

    Scaffold(
        bottomBar = {
            if (topLevelDestination != null) {
                SymptomTrackerBottomBar(
                    destinations = appState.topLevelDestinations,
                    currentDestination = topLevelDestination,
                    onNavigateToDestination = appState::navigateToTopLevelDestination
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Show the top bar on top level destinations
            if (topLevelDestination != null) {
                SymptomTrackerTopLevelTopAppBar(titleId = topLevelDestination.titleTextId)
            }

            SymptomTrackerNavHost(navController = appState.navController)
        }
    }
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
        logsScreen(
            onAddFoodClick = { navController.navigate(Route.ADD_FOOD.name) },
            onAddSymptomClick = { navController.navigate(Route.ADD_SYMPTOM.name) },
            onAddMovementClick = { navController.navigate(Route.ADD_MOVEMENT.name) },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymptomTrackerTopLevelTopAppBar(
    @StringRes titleId: Int,
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        title = { Text(text = stringResource(id = titleId)) },
        modifier = modifier,
    )
}

@Composable
fun SymptomTrackerBottomBar(
    destinations: List<TopLevelDestination>,
    currentDestination: TopLevelDestination?,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
) {
    NavigationBar {
        destinations.forEach { destination ->
            NavigationBarItem(
                selected = currentDestination?.equals(destination) ?: false,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    Icon(
                        painter = painterResource(id = destination.iconId),
                        contentDescription = null
                    )
                },
                label = { Text(text = stringResource(id = destination.iconTextId)) }
            )
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
