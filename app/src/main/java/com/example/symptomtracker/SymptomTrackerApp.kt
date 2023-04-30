import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.symptomtracker.R

enum class Route {
    HOME, ADD_FOOD, ADD_SYMPTOM, ADD_MOVEMENT
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
            HomeScreen(onAddFoodClick = { navController.navigate(Route.ADD_FOOD.name) },
                onAddSymptomClick = { navController.navigate(Route.ADD_SYMPTOM.name) },
                onAddMovementClick = { navController.navigate(Route.ADD_MOVEMENT.name) })
        }
        composable(route = Route.ADD_FOOD.name) {
            AddFoodScreen(onBackClick = { navController.navigateUp() })
        }
        composable(route = Route.ADD_SYMPTOM.name) {
            AddSymptomScreen(navigateBack = { navController.navigateUp() })
        }
        composable(route = Route.ADD_MOVEMENT.name) {
            AddMovementScreen(onBackClick = { navController.navigateUp() })
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
    actions: @Composable() (RowScope.() -> Unit) = {},
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
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back_button_cd)
                )
            }
        },
            actions = actions,
            modifier = modifier
        )
    } else {
        CenterAlignedTopAppBar(title = {
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