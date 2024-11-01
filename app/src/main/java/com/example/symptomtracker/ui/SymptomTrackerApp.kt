package com.example.symptomtracker.ui

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
import com.example.symptomtracker.R
import com.example.symptomtracker.navigation.SymptomTrackerNavHost
import com.example.symptomtracker.navigation.TopLevelDestination

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
