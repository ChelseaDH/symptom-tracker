package com.example.symptomtracker.feature.drink

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.core.domain.repository.DrinkLogRepository
import com.example.symptomtracker.navigation.DATE_ARG
import com.example.symptomtracker.navigation.PREFILL_ITEMS
import com.example.symptomtracker.navigation.decodePrefillItems
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddDrinkViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val drinkLogRepository: DrinkLogRepository,
) : AbstractDrinkEntryViewModel(drinkLogRepository) {
    private val prefillItems: String? = savedStateHandle[PREFILL_ITEMS]
    private val dateArg: String? = savedStateHandle[DATE_ARG]

    init {
        viewModelScope.launch {
            setUiStateFromPrefill(
                items = decodePrefillItems(prefillItems),
                date = dateArg,
            )
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
