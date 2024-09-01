package com.example.symptomtracker.feature.food

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.core.domain.repository.FoodLogRepository
import com.example.symptomtracker.core.domain.model.FoodItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageItemsViewModel @Inject constructor(private val foodLogRepository: FoodLogRepository) :
    ViewModel() {
    val foodItemsState: StateFlow<FoodItemsUiState> =
        foodLogRepository.getAllItems().map { items -> FoodItemsUiState.Data(items) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = FoodItemsUiState.Loading
            )

    val userActionState = MutableStateFlow<ActionState?>(null)

    fun startAction(foodItem: FoodItem, action: FoodItemAction) {
        when (action) {
            FoodItemAction.EDIT -> userActionState.value =
                ActionState.Edit(foodItem = foodItem)

            FoodItemAction.DELETE -> {
                viewModelScope.launch {
                    userActionState.value =
                        if (foodLogRepository.getCountOfLogsItemBelongsTo(foodItem) == 0) {
                            ActionState.Delete.Direct(foodItem = foodItem)
                        } else {
                            ActionState.Delete.Merge(
                                foodItem = foodItem,
                                mergeCandidates = (foodItemsState.value as FoodItemsUiState.Data).items.filter { it != foodItem }
                            )
                        }
                }
            }
        }
    }

    fun cancelAction() {
        userActionState.value = null
    }

    fun onEditNameChange(newName: String) {
        userActionState.value =
            (userActionState.value as? ActionState.Edit)?.copy(
                name = newName,
                canSubmit = newName.isNotBlank()
            )
    }

    fun onMergeCandidateChosen(chosenItem: FoodItem) {
        userActionState.value =
            (userActionState.value as? ActionState.Delete.Merge)?.copy(
                chosenItem = chosenItem,
                canSubmit = true
            )
    }

    fun submitAction() {
        viewModelScope.launch {
            when (val actionState = userActionState.value) {
                is ActionState.Edit -> {
                    foodLogRepository.updateFoodItem(actionState.foodItem.copy(name = actionState.name))
                }

                is ActionState.Delete.Direct -> {
                    foodLogRepository.deleteFoodItem(actionState.foodItem)
                }

                is ActionState.Delete.Merge -> {
                    if (actionState.chosenItem == null) return@launch

                    foodLogRepository.mergeFoodItems(
                        foodItem = actionState.chosenItem,
                        foodItemToMerge = actionState.foodItem
                    )
                }

                null -> {}
            }
        }
        cancelAction()
    }
}

sealed interface FoodItemsUiState {
    data object Loading : FoodItemsUiState
    data class Data(val items: List<FoodItem>) : FoodItemsUiState
}

sealed class ActionState {
    data class Edit(
        val foodItem: FoodItem,
        val name: String = foodItem.name,
        val canSubmit: Boolean = true,
    ) : ActionState()

    sealed class Delete : ActionState() {
        data class Direct(val foodItem: FoodItem) : Delete()
        data class Merge(
            val foodItem: FoodItem,
            val mergeCandidates: List<FoodItem>,
            val chosenItem: FoodItem? = null,
            val canSubmit: Boolean = false,
        ) : Delete()
    }
}

enum class FoodItemAction {
    EDIT,
    DELETE,
}
