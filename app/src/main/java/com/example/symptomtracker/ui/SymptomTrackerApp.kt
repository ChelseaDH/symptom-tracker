package com.example.symptomtracker.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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
        },
        topBar = {
            if (topLevelDestination != null) {
                SymptomTrackerTopLevelTopAppBar(titleId = topLevelDestination.titleTextId)
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(WindowInsets.displayCutout)
        ) {
            SymptomTrackerNavHost(navController = appState.navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
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
            val isSelected = currentDestination?.equals(destination) ?: false

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    Icon(
                        painter = painterResource(id = if (isSelected) destination.activeIconId else destination.iconId),
                        contentDescription = null,
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
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit = {},
    actions: @Composable (RowScope.() -> Unit) = {},
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = { navigateUp() }) {
                Icon(
                    painter = painterResource(R.drawable.outline_arrow_back_24),
                    contentDescription = stringResource(R.string.back_button_cd)
                )
            }
        },
        actions = actions,
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
fun BottomBarOnHomePreview() {
    SymptomTrackerBottomBar(
        destinations = TopLevelDestination.entries,
        currentDestination = TopLevelDestination.HOME,
        onNavigateToDestination = {}
    )
}

@Preview(showBackground = true)
@Composable
fun BottomBarOnLogsPreview() {
    SymptomTrackerBottomBar(
        destinations = TopLevelDestination.entries,
        currentDestination = TopLevelDestination.LOGS,
        onNavigateToDestination = {}
    )
}

@Preview(showBackground = true)
@Composable
fun BottomBarOnSettingsPreview() {
    SymptomTrackerBottomBar(
        destinations = TopLevelDestination.entries,
        currentDestination = TopLevelDestination.SETTINGS,
        onNavigateToDestination = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun TopAppBarPreview() {
    SymptomTrackerTopAppBar(
        title = "Title",
    )
}

@Preview(showBackground = true)
@Composable
fun TopLevelTopAppBarPreview() {
    SymptomTrackerTopLevelTopAppBar(
        titleId = R.string.app_name,
    )
}
