package com.example.symptomtracker.feature.food

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.core.domain.repository.FoodLogRepository
import com.example.symptomtracker.feature.food.navigation.PREFILL_ITEMS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.net.URLDecoder
import javax.inject.Inject

@HiltViewModel
class AddFoodViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val foodLogRepository: FoodLogRepository
) : AbstractFoodEntryViewModel(foodLogRepository) {
    private val prefillItems: String? = savedStateHandle[PREFILL_ITEMS]

    init {
        viewModelScope.launch {
            if (prefillItems != null) {
                setUiStateFromPrefill(
                    URLDecoder.decode(prefillItems, "UTF-8").removeSurrounding("[", "]").split(",")
                )
            }
        }
    }

    override fun submit() {
        if (uiState.isValid()) {
            viewModelScope.launch {
                foodLogRepository.insertFoodLog(uiState.toFoodLog())
            }
        }
    }
}
