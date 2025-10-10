package com.example.symptomtracker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.symptomtracker.feature.drink.navigation.addDrinkScreen
import com.example.symptomtracker.feature.drink.navigation.navigateToAddDrink
import com.example.symptomtracker.feature.food.navigation.addFoodScreen
import com.example.symptomtracker.feature.food.navigation.editFoodScreen
import com.example.symptomtracker.feature.food.navigation.manageFoodItemsScreen
import com.example.symptomtracker.feature.food.navigation.navigateToAddFood
import com.example.symptomtracker.feature.food.navigation.navigateToEditFood
import com.example.symptomtracker.feature.food.navigation.navigateToManageFoodItems
import com.example.symptomtracker.feature.food.navigation.navigateToViewFood
import com.example.symptomtracker.feature.food.navigation.viewFoodScreen
import com.example.symptomtracker.feature.home.navigation.HOME_ROUTE
import com.example.symptomtracker.feature.home.navigation.homeScreen
import com.example.symptomtracker.feature.logs.navigation.logsScreen
import com.example.symptomtracker.feature.mealie.navigation.mealieImportScreen
import com.example.symptomtracker.feature.mealie.navigation.mealieSettingsScreen
import com.example.symptomtracker.feature.mealie.navigation.navigateToMealieImport
import com.example.symptomtracker.feature.mealie.navigation.navigateToMealieSettings
import com.example.symptomtracker.feature.movement.navigation.addMovementScreen
import com.example.symptomtracker.feature.movement.navigation.editMovementScreen
import com.example.symptomtracker.feature.movement.navigation.navigateToAddMovement
import com.example.symptomtracker.feature.movement.navigation.navigateToEditMovement
import com.example.symptomtracker.feature.movement.navigation.navigateToViewMovement
import com.example.symptomtracker.feature.movement.navigation.viewMovementScreen
import com.example.symptomtracker.feature.settings.navigation.settingsScreen
import com.example.symptomtracker.feature.symptom.navigation.addSymptomScreen
import com.example.symptomtracker.feature.symptom.navigation.editSymptomScreen
import com.example.symptomtracker.feature.symptom.navigation.navigateToAddSymptom
import com.example.symptomtracker.feature.symptom.navigation.navigateToEditSymptom
import com.example.symptomtracker.feature.symptom.navigation.navigateToViewSymptom
import com.example.symptomtracker.feature.symptom.navigation.viewSymptomScreen

@Composable
fun SymptomTrackerNavHost(
    navController: NavHostController,
) {
    NavHost(navController = navController, startDestination = HOME_ROUTE) {
        homeScreen(
            navigateToAddFood = navController::navigateToAddFood,
            navigateToAddDrink = navController::navigateToAddDrink,
            navigateToAddSymptom = navController::navigateToAddSymptom,
            navigateToAddMovement = navController::navigateToAddMovement,
            onFoodClick = navController::navigateToViewFood,
            onSymptomClick = navController::navigateToViewSymptom,
            onMovementClick = navController::navigateToViewMovement,
            navigateToMealieImport = navController::navigateToMealieImport,
        )
        logsScreen(
            onFoodClick = navController::navigateToViewFood,
            onAddFoodClick = navController::navigateToAddFood,
            onSymptomClick = navController::navigateToViewSymptom,
            onAddSymptomClick = navController::navigateToAddSymptom,
            onMovementClick = navController::navigateToViewMovement,
            onAddMovementClick = navController::navigateToAddMovement,
            onMealieImportClick = navController::navigateToMealieImport,
        )
        settingsScreen(
            navigateToManageFoodItems = navController::navigateToManageFoodItems,
            navigateToMealieSettings = navController::navigateToMealieSettings,
        )

        viewFoodScreen(
            navigateBack = navController::navigateUp,
            navigateToEdit = navController::navigateToEditFood,
            navigateToCopy = navController::navigateToAddFood,
        )
        addFoodScreen(navigateBack = navController::navigateUp)
        editFoodScreen(navigateBack = navController::navigateUp)
        manageFoodItemsScreen(navigateBack = navController::navigateUp)

        addDrinkScreen(navigateBack = navController::navigateUp)

        viewSymptomScreen(
            navigateBack = navController::navigateUp,
            navigateToEdit = navController::navigateToEditSymptom,
        )
        addSymptomScreen(navigateBack = navController::navigateUp)
        editSymptomScreen(navigateBack = navController::navigateUp)

        viewMovementScreen(
            navigateBack = navController::navigateUp,
            navigateToEdit = navController::navigateToEditMovement,
        )
        addMovementScreen(navigateBack = navController::navigateUp)
        editMovementScreen(navigateBack = navController::navigateUp)

        mealieImportScreen(
            navigateBack = navController::navigateUp,
            navigateToAddFood = navController::navigateToAddFood,
        )
        mealieSettingsScreen(navigateBack = navController::navigateUp)
    }
}
