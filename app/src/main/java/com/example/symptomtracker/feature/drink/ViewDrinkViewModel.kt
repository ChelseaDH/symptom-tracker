package com.example.symptomtracker.feature.drink

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.core.domain.model.DrinkLog
import com.example.symptomtracker.core.domain.repository.DrinkLogRepository
import com.example.symptomtracker.core.ui.ViewLogUiState
import com.example.symptomtracker.feature.drink.navigation.DRINK_LOG_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewDrinkViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val drinkLogRepository: DrinkLogRepository
) : ViewModel() {
    internal val logId: Long = checkNotNull(savedStateHandle[DRINK_LOG_ID])

    val uiState: StateFlow<ViewDrinkUiState> = drinkLogRepository.getDrinkLog(logId).map { log ->
        if (log !== null) {
            ViewLogUiState.Data(log)
        } else {
            ViewLogUiState.Empty
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ViewLogUiState.Loading
    )

    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent

    fun handleEvent(event: ViewDrinkEvent) {
        when (event) {
            is ViewDrinkEvent.DeleteLog -> deleteLog(drinkLog = event.drinkLog)
            ViewDrinkEvent.EditLog -> _navigationEvent.value = NavigationEvent.NavigateToEdit
            ViewDrinkEvent.CopyLog -> _navigationEvent.value =
                NavigationEvent.NavigateToCopy((uiState.value as ViewLogUiState.Data).log)

            ViewDrinkEvent.NavigationHandled -> _navigationEvent.value = null
        }
    }

    private fun deleteLog(drinkLog: DrinkLog) {
        viewModelScope.launch {
            drinkLogRepository.deleteDrinkLog(drinkLog)
        }
    }
}

typealias ViewDrinkUiState = ViewLogUiState<DrinkLog>

sealed interface ViewDrinkEvent {
    data class DeleteLog(val drinkLog: DrinkLog) : ViewDrinkEvent
    data object EditLog : ViewDrinkEvent
    data object CopyLog : ViewDrinkEvent
    data object NavigationHandled : ViewDrinkEvent
}

sealed class NavigationEvent {
    data object NavigateToEdit : NavigationEvent()
    data class NavigateToCopy(val drinkLog: DrinkLog) : NavigationEvent()
}
