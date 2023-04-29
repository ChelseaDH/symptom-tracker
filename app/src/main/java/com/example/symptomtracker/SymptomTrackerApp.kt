import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

enum class Route {
    HOME, ADD_FOOD, ADD_SYMPTOM, ADD_MOVEMENT
}

@Composable
fun SymptomTrackerApp() {
    val navController = rememberNavController()
    SymptomTrackerNavHost(navController = navController)
}

@Composable
fun SymptomTrackerNavHost(navController: NavHostController) {
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
            AddSymptomScreen(onBackClick = { navController.navigateUp() })
        }
        composable(route = Route.ADD_MOVEMENT.name) {
            AddMovementScreen(onBackClick = { navController.navigateUp() })
        }
    }
}