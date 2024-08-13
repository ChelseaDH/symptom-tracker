package com.example.symptomtracker.feature.food

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.core.data.repository.FoodLogRepository
import com.example.symptomtracker.core.model.FoodItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageItemsViewModel @Inject constructor(private val foodLogRepository: FoodLogRepository) :
    ViewModel() {
    val uiState: StateFlow<ManageItemsUiState> =
        foodLogRepository.getAllItems().map { items -> ManageItemsUiState.Data(items) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ManageItemsUiState.Loading
            )

    fun editItemName(item: FoodItem, name: String) {
        viewModelScope.launch {
            foodLogRepository.updateFoodItem(foodItem = item.copy(name = name))
        }
    }
}

sealed interface ManageItemsUiState {
    data object Loading : ManageItemsUiState
    data class Data(val items: List<FoodItem>) : ManageItemsUiState
}
