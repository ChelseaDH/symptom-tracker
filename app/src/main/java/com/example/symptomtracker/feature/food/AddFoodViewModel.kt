package com.example.symptomtracker.feature.food

import com.example.symptomtracker.core.data.repository.FoodLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddFoodViewModel @Inject constructor(private val foodLogRepository: FoodLogRepository) :
    AbstractFoodEntryViewModel(foodLogRepository) {
    override suspend fun submit() {
        if (uiState.isValid()) {
            foodLogRepository.insertFoodLogWithItems(uiState.toFoodLogWithItems())
        }
    }
}
