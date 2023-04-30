package com.example.symptomtracker.ui.symptom

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.symptomtracker.data.symptom.Symptom
import com.example.symptomtracker.data.symptom.SymptomRepository
import java.util.*

/**
 * View Model to validate and insert symptoms in the Room database.
 */
class SymptomEntryViewModel(private val symptomRepository: SymptomRepository) : ViewModel() {
    /**
     * Holds current Symptom Ui state.
     */
    var symptomUiState by mutableStateOf(SymptomUiState())
        private set

    /**
     * Updates the [SymptomUiState] and triggers validation for the input values.
     */
    fun updateUiState(symptomDetails: SymptomDetails) {
        symptomUiState =
            SymptomUiState(symptomDetails = symptomDetails,
                isEntryValid = validateInput(symptomDetails))
    }

    /**
     * Inserts a [Symptom] in the Room database.
     */
    suspend fun saveSymptom() {
        if (validateInput()) {
            symptomRepository.insertSymptom(symptomUiState.symptomDetails.toSymptom())
        }
    }

    private fun validateInput(uiState: SymptomDetails = symptomUiState.symptomDetails): Boolean {
        return with(uiState) {
            type.isNotBlank()
        }
    }
}

/**
 * Represents the Ui state for a Symptom.
 */
data class SymptomUiState(
    val symptomDetails: SymptomDetails = SymptomDetails(),
    val isEntryValid: Boolean = false,
)

data class SymptomDetails(
    val id: Int = 0,
    val type: String = "",
)

/**
 * Extension function to convert [SymptomUiState] to [Symptom].
 */
fun SymptomDetails.toSymptom(): Symptom = Symptom(
    id = id,
    type = SymptomType.valueOf(type.uppercase()),
    date = Date()
)

