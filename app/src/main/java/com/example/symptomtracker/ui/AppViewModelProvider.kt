package com.example.symptomtracker.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.symptomtracker.SymptomTrackerApplication
import com.example.symptomtracker.ui.food.FoodEntryViewModel
import com.example.symptomtracker.ui.home.HomeScreenViewModel
import com.example.symptomtracker.ui.logs.LogsViewModel
import com.example.symptomtracker.ui.movement.MovementEntryViewModel
import com.example.symptomtracker.ui.symptom.SymptomEntryViewModel

/**
 * Provides Factory to create an instance of ViewModel for the entire Symptom Tracker app.
 */
object AppViewModelProvider {
    val Factory: ViewModelProvider.Factory = viewModelFactory {
        initializer {
            SymptomEntryViewModel(symptomRepository = symptomTrackerApplication().container.symptomRepository)
        }

        initializer {
            FoodEntryViewModel(foodLogRepository = symptomTrackerApplication().container.foodLogRepository)
        }

        initializer {
            MovementEntryViewModel(movementRepository = symptomTrackerApplication().container.movementRepository)
        }

        initializer {
            HomeScreenViewModel(
                foodLogRepository = symptomTrackerApplication().container.foodLogRepository,
                symptomRepository = symptomTrackerApplication().container.symptomRepository,
                movementRepository = symptomTrackerApplication().container.movementRepository,
            )
        }

        initializer {
            LogsViewModel(
                foodLogRepository = symptomTrackerApplication().container.foodLogRepository,
                symptomRepository = symptomTrackerApplication().container.symptomRepository,
                movementRepository = symptomTrackerApplication().container.movementRepository,
            )
        }
    }
}

/**
 * Extension function that queries for [Application] object and returns an instance of [SymptomTrackerApplication].
 */
fun CreationExtras.symptomTrackerApplication(): SymptomTrackerApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as SymptomTrackerApplication)
