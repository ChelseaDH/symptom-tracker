package com.example.symptomtracker.feature.drink

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.core.domain.repository.DrinkLogRepository
import com.example.symptomtracker.feature.drink.navigation.PREFILL_ITEMS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddDrinkViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val drinkLogRepository: DrinkLogRepository
) : AbstractDrinkEntryViewModel(drinkLogRepository) {
    private val prefillItems: String? = savedStateHandle[PREFILL_ITEMS]

    init {
        viewModelScope.launch {
            if (prefillItems != null) {
                setUiStateFromPrefill(prefillItems.removeSurrounding("[", "]").split(","))
            }
        }
    }

    override fun submit() {
        if (uiState.isValid()) {
            viewModelScope.launch {
                drinkLogRepository.insertDrinkLog(uiState.toDrinkLog())
            }
        }
    }
}
