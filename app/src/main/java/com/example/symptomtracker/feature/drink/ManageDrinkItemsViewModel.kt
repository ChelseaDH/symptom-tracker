package com.example.symptomtracker.feature.drink

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.core.domain.model.DrinkItem
import com.example.symptomtracker.core.domain.repository.DrinkLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageDrinkItemsViewModel @Inject constructor(private val drinkLogRepository: DrinkLogRepository) :
    ViewModel() {
    val drinkItemsState: StateFlow<DrinkItemsUiState> =
        drinkLogRepository.getAllItems().map { items -> DrinkItemsUiState.Data(items) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DrinkItemsUiState.Loading
        )

    val userActionState = MutableStateFlow<DrinkActionState?>(null)

    fun handleEvent(event: ManageDrinkEvent) {
        when (event) {
            is ManageDrinkEvent.StartAction -> startAction(
                drinkItem = event.drinkItem, action = event.action
            )

            ManageDrinkEvent.CancelAction -> cancelAction()
            is ManageDrinkEvent.ChooseMergeCandidate -> updateChosenMergeCandidate(chosenItem = event.chosenItem)
            is ManageDrinkEvent.UpdateName -> updateName(newName = event.name)
            ManageDrinkEvent.SubmitAction -> submitAction()
        }
    }

    private fun startAction(drinkItem: DrinkItem, action: DrinkItemAction) {
        when (action) {
            DrinkItemAction.EDIT -> userActionState.value = DrinkActionState.Edit(drinkItem = drinkItem)

            DrinkItemAction.DELETE -> {
                viewModelScope.launch {
                    userActionState.value =
                        if (drinkLogRepository.getCountOfLogsItemBelongsTo(drinkItem) == 0) {
                            DrinkActionState.Delete.Direct(drinkItem = drinkItem)
                        } else {
                            DrinkActionState.Delete.Merge(drinkItem = drinkItem,
                                mergeCandidates = (drinkItemsState.value as DrinkItemsUiState.Data).items.filter { it != drinkItem })
                        }
                }
            }
        }
    }

    private fun cancelAction() {
        userActionState.value = null
    }

    private fun updateName(newName: String) {
        userActionState.value = (userActionState.value as? DrinkActionState.Edit)?.copy(
            name = newName, canSubmit = newName.isNotBlank()
        )
    }

    private fun updateChosenMergeCandidate(chosenItem: DrinkItem) {
        userActionState.value = (userActionState.value as? DrinkActionState.Delete.Merge)?.copy(
            chosenItem = chosenItem, canSubmit = true
        )
    }

    private fun submitAction() {
        viewModelScope.launch {
            when (val actionState = userActionState.value) {
                is DrinkActionState.Edit -> {
                    drinkLogRepository.updateDrinkItem(actionState.drinkItem.copy(name = actionState.name))
                }

                is DrinkActionState.Delete.Direct -> {
                    drinkLogRepository.deleteDrinkItem(actionState.drinkItem)
                }

                is DrinkActionState.Delete.Merge -> {
                    if (actionState.chosenItem == null) return@launch

                    drinkLogRepository.mergeDrinkItems(
                        drinkItem = actionState.chosenItem, drinkItemToMerge = actionState.drinkItem
                    )
                }

                null -> {}
            }
        }
        cancelAction()
    }
}

sealed interface DrinkItemsUiState {
    data object Loading : DrinkItemsUiState
    data class Data(val items: List<DrinkItem>) : DrinkItemsUiState
}

sealed class DrinkActionState {
    data class Edit(
        val drinkItem: DrinkItem,
        val name: String = drinkItem.name,
        val canSubmit: Boolean = true,
    ) : DrinkActionState()

    sealed class Delete : DrinkActionState() {
        data class Direct(val drinkItem: DrinkItem) : Delete()
        data class Merge(
            val drinkItem: DrinkItem,
            val mergeCandidates: List<DrinkItem>,
            val chosenItem: DrinkItem? = null,
            val canSubmit: Boolean = false,
        ) : Delete()
    }
}

enum class DrinkItemAction {
    EDIT, DELETE,
}

sealed interface ManageDrinkEvent {
    data class StartAction(val drinkItem: DrinkItem, val action: DrinkItemAction) : ManageDrinkEvent
    data object CancelAction : ManageDrinkEvent
    data class UpdateName(val name: String) : ManageDrinkEvent
    data class ChooseMergeCandidate(val chosenItem: DrinkItem) : ManageDrinkEvent
    data object SubmitAction : ManageDrinkEvent
}
