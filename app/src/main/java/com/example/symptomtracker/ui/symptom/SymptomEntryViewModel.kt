package com.example.symptomtracker.ui.symptom

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.data.symptom.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

/**
 * View Model to validate and insert symptoms in the Room database.
 */
class SymptomEntryViewModel(private val symptomRepository: SymptomRepository) : ViewModel() {
    var uiState by mutableStateOf(SymptomUiState())
        private set

    private var _allSymptoms = listOf<Symptom>()
    private val _selectedSymptoms = listOf<SymptomWithSeverity>().toMutableStateList()

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

    fun addSymptomWithSeverityToLog() {
        if (validateCanAddSymptomToLog()) {
            _selectedSymptoms.add(uiState.toSymptomWithSeverity())

            uiState = uiState.copy(
                symptomLogDetails = SymptomLogDetails(_selectedSymptoms)
            )
            clearSymptomInputs()
        }
    }

    fun removeSymptomFromLog(symptomsWithSeverity: SymptomWithSeverity) {
        _selectedSymptoms.remove(symptomsWithSeverity)

        uiState = uiState.copy(
            symptomLogDetails = SymptomLogDetails(_selectedSymptoms)
        )
    }

    fun updateSelectedSymptom(symptom: Symptom) {
        uiState = uiState.copy(
            symptomInput = uiState.symptomInput.copy(
                name = symptom.name,
                symptom = symptom,
            ),
            availableSymptoms = getAvailableSymptoms(symptom.name),
            canCreateSymptomFromInput = false
        )
    }

    fun updateSelectedSymptomName(symptomName: String) {
        uiState = uiState.copy(
            symptomInput = uiState.symptomInput.copy(
                name = symptomName,
                symptom = null,
            ),
            availableSymptoms = getAvailableSymptoms(symptomName),
            canCreateSymptomFromInput = canCreateNewSymptomFromInput(symptomName)
        )
    }

    fun clearSymptomInputs() {
        uiState = uiState.copy(
            symptomInput = uiState.symptomInput.copy(
                name = "",
                symptom = null,
                severity = null,
            ),
            availableSymptoms = getAvailableSymptoms("")
        )
    }

    fun updateSelectedSeverity(severity: Severity) {
        uiState = uiState.copy(
            symptomInput = uiState.symptomInput.copy(
                severity = severity
            ),
        )
    }

    /**
     * Inserts a [Symptom] in the Room database and updates selected symptom to the newly inserted symptom.
     */
    fun insertSymptom() {
        if (validateSymptomInput()) {
            val symptom = uiState.toSymptom()

            viewModelScope.launch(Dispatchers.IO) {
                symptomRepository.insertSymptom(symptom)
                updateSelectedSymptom(symptom)
            }
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
        return symptomLogDetails.symptomsWithSeverity.isNotEmpty() && symptomLogDetails.symptomsWithSeverity.none { it.symptom.name.isBlank() }
    }

    private fun validateSymptomInput(symptomName: String = this.uiState.symptomInput.name): Boolean {
        return symptomName.isNotEmpty()
    }

    private fun validateCanAddSymptomToLog(symptomInput: SymptomInput = this.uiState.symptomInput): Boolean {
        return symptomInput.symptom != null
                && symptomInput.severity != null
                && _selectedSymptoms.none { it.symptom == symptomInput.symptom }
    }

    private fun canCreateNewSymptomFromInput(symptomName: String = this.uiState.symptomInput.name): Boolean {
        return symptomName.isNotBlank() && _allSymptoms.none {
            it.name.equals(symptomName, ignoreCase = true)
        }
    }

    private fun getAvailableSymptoms(selectedSymptomName: String = uiState.symptomInput.name): List<Symptom> {
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
    val symptomInput: SymptomInput = SymptomInput(),
    val canCreateSymptomFromInput: Boolean = false,
    val isEntryValid: Boolean = false,
)

data class SymptomLogDetails(
    val symptomsWithSeverity: List<SymptomWithSeverity> = listOf(),
)

data class SymptomInput(
    val name: String = "",
    val severity: Severity? = null,
    val symptom: Symptom? = null,
)

/**
 * Extension function to convert [SymptomUiState] to [Symptom].
 */
fun SymptomUiState.toSymptom(): Symptom = Symptom(
    symptomId = 0,
    name = symptomInput.name.replaceFirstChar { it.uppercaseChar() }
)

fun SymptomUiState.toSymptomLogWithSymptoms(): SymptomLogWithSymptoms = SymptomLogWithSymptoms(
    symptomLog = SymptomLog(symptomLogId = 0, date = Date()),
    symptomWithSeverities = symptomLogDetails.symptomsWithSeverity
)

fun SymptomUiState.toSymptomWithSeverity(): SymptomWithSeverity = SymptomWithSeverity(
    symptom = symptomInput.symptom!!,
    severity = symptomInput.severity!!
)

