package com.example.symptomtracker.core.ui

import com.example.symptomtracker.core.model.Log

sealed interface ViewLogUiState<out L : Log> {
    data object Loading : ViewLogUiState<Nothing>
    data class Data<L : Log>(val log: L) : ViewLogUiState<L>
    data object Empty : ViewLogUiState<Nothing>
}
