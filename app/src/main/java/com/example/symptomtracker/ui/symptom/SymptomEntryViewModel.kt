package com.example.symptomtracker.ui.symptom

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.data.symptom.Symptom
import com.example.symptomtracker.data.symptom.SymptomLog
import com.example.symptomtracker.data.symptom.SymptomLogWithSymptoms
import com.example.symptomtracker.data.symptom.SymptomRepository
import kotlinx.coroutines.launch
import java.util.*

/**
 * View Model to validate and insert symptoms in the Room database.
 */
class SymptomEntryViewModel(private val symptomRepository: SymptomRepository) : ViewModel() {
    var uiState by mutableStateOf(SymptomUiState())
        private set

    private var _allSymptoms = listOf<Symptom>()
    private val _selectedSymptoms = listOf<Symptom>().toMutableStateList()
    private var _selectedSymptom: Symptom? = null

    init {
        viewModelScope.launch {
            symptomRepository.getAllSymptomsStream().collect {
                _allSymptoms = it
                uiState = uiState.copy(
                    availableSymptoms = getAvailableSymptoms()
                )
            }
        }
    }

    fun addSymptomToLog() {
        if (_selectedSymptom !== null && !_selectedSymptoms.contains(_selectedSymptom)) {
            _selectedSymptoms.add(_selectedSymptom!!)

            uiState = uiState.copy(
                symptomLogDetails = SymptomLogDetails(_selectedSymptoms)
            )
            clearSymptomInputs()
        }
    }

    fun removeSymptomFromLog(symptom: Symptom) {
        _selectedSymptoms.remove(symptom)

        uiState = uiState.copy(
            symptomLogDetails = SymptomLogDetails(_selectedSymptoms)
        )
    }

    fun updateSelectedSymptom(symptom: Symptom) {
        _selectedSymptom = symptom

        uiState = uiState.copy(
            selectedSymptomName = symptom.name,
            availableSymptoms = getAvailableSymptoms(symptom.name),
            canCreateSymptomFromInput = false
        )
    }

    fun updateSelectedSymptomName(symptomName: String) {
        _selectedSymptom = null

        uiState = uiState.copy(
            selectedSymptomName = symptomName,
            availableSymptoms = getAvailableSymptoms(symptomName),
            canCreateSymptomFromInput = canCreateNewSymptomFromInput(symptomName)
        )
    }

    fun clearSymptomInputs() {
        _selectedSymptom = null
        uiState = uiState.copy(
            selectedSymptomName = "",
            availableSymptoms = getAvailableSymptoms("")
        )
    }

    /**
     * Inserts a [Symptom] in the Room database and updates selected symptom to the newly inserted symptom.
     */
    suspend fun insertSymptom() {
        if (validateSymptomInput()) {
            val symptom = uiState.toSymptom()

            symptomRepository.insertSymptom(symptom)
            updateSelectedSymptom(symptom)
        }
    }

    /**
     * Inserts a [SymptomLogWithSymptoms] in the Room database.
     */
    suspend fun insertSymptomLog() {
        if (validateSymptomLogInput()) {
            symptomRepository.insertSymptomLogWithSymptom(uiState.toSymptomLogWithSymptoms())
        }
    }

    private fun validateSymptomLogInput(symptomLogDetails: SymptomLogDetails = this.uiState.symptomLogDetails): Boolean {
        return symptomLogDetails.symptoms.isNotEmpty() && symptomLogDetails.symptoms.none { it.name.isBlank() }
    }

    private fun validateSymptomInput(symptomName: String = this.uiState.selectedSymptomName): Boolean {
        return symptomName.isNotEmpty()
    }

    private fun canCreateNewSymptomFromInput(symptomName: String = this.uiState.selectedSymptomName): Boolean {
        return symptomName.isNotBlank() && _allSymptoms.none {
            it.name.equals(symptomName, ignoreCase = true)
        }
    }

    private fun getAvailableSymptoms(selectedSymptomName: String = uiState.selectedSymptomName): List<Symptom> {
        return _allSymptoms.filter { symptom ->
            symptom.name.contains(selectedSymptomName, ignoreCase = true)
        }
    }
}

/**
 * Represents the Ui state for a Symptom.
 */
data class SymptomUiState(
    val symptomLogDetails: SymptomLogDetails = SymptomLogDetails(),
    val availableSymptoms: List<Symptom> = listOf(),
    val selectedSymptomName: String = "",
    val canCreateSymptomFromInput: Boolean = false,
    val isEntryValid: Boolean = false,
)

data class SymptomLogDetails(
    val symptoms: List<Symptom> = listOf(),
)

/**
 * Extension function to convert [SymptomUiState] to [Symptom].
 */
fun SymptomUiState.toSymptom(): Symptom = Symptom(
    symptomId = 0,
    name = selectedSymptomName.replaceFirstChar { it.uppercaseChar() }
)

fun SymptomUiState.toSymptomLogWithSymptoms(): SymptomLogWithSymptoms = SymptomLogWithSymptoms(
    symptomLog = SymptomLog(symptomLogId = 0, date = Date()),
    symptoms = symptomLogDetails.symptoms
)

