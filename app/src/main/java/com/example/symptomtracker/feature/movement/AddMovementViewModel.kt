package com.example.symptomtracker.feature.movement

import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.core.domain.model.MovementLog
import com.example.symptomtracker.core.domain.repository.MovementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMovementViewModel @Inject constructor(private val movementRepository: MovementRepository) :
    AbstractMovementEntryViewModel() {
    override fun submit() {
        if (uiState.isValid()) {
            viewModelScope.launch(Dispatchers.IO) {
                movementRepository.insertMovementLog(uiState.toMovementLog())
            }
        }
    }
}

fun MovementEntryUiState.toMovementLog(id: Long = 0): MovementLog {
    return MovementLog(
        id = id,
        date = dateTimeInput.toDate(),
        stoolType = chosenStoolType!!
    )
}
