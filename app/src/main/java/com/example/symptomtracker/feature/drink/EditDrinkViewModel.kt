package com.example.symptomtracker.feature.drink

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.core.domain.repository.DrinkLogRepository
import com.example.symptomtracker.feature.drink.navigation.DRINK_LOG_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditDrinkViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val drinkLogRepository: DrinkLogRepository,
) : AbstractDrinkEntryViewModel(drinkLogRepository) {
    internal val logId: Long = checkNotNull(savedStateHandle[DRINK_LOG_ID])

    init {
        viewModelScope.launch {
            drinkLogRepository.getDrinkLog(logId).collect { log ->
                if (log !== null) {
                    setUiStateWithLog(log)
                }
            }
        }
    }

    override fun submit() {
        if (uiState.isValid()) {
            viewModelScope.launch {
                drinkLogRepository.updateDrinkLog(uiState.toDrinkLog(logId))
            }
        }
    }
}
