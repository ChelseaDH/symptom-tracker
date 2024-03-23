package com.example.symptomtracker.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class HomeScreenViewModel : ViewModel() {
    var uiState by mutableStateOf(UiState())
        private set

    fun updateBottomSheetVisibility(visible: Boolean) {
        uiState = uiState.copy(
            showBottomSheet = visible
        )
    }
}

data class UiState(
    val showBottomSheet: Boolean = false
)
