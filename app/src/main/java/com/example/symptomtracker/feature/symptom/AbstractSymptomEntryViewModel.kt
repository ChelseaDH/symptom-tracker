package com.example.symptomtracker.feature.symptom

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.core.designsystem.component.DateInputFields
import com.example.symptomtracker.core.designsystem.component.DateTimeInput
import com.example.symptomtracker.core.designsystem.component.TimeInputFields
import com.example.symptomtracker.core.designsystem.component.toDate
import com.example.symptomtracker.core.domain.repository.SymptomRepository
import com.example.symptomtracker.core.domain.model.Severity
import com.example.symptomtracker.core.domain.model.Symptom
import com.example.symptomtracker.core.domain.model.SymptomLog
import com.example.symptomtracker.core.domain.model.SymptomWithSeverity
import com.example.symptomtracker.core.util.toFoodItemName
import kotlinx.coroutines.launch
import java.util.Calendar

abstract class AbstractSymptomEntryViewModel(private val symptomRepository: SymptomRepository) :
    ViewModel() {
    var uiState by mutableStateOf(SymptomEntryUiState(Calendar.getInstance()))
        private set

    private var _allSymptoms = listOf<Symptom>()
    private var _selectedSymptoms = listOf<SymptomWithSeverity>().toMutableStateList()

    init {
        viewModelScope.launch {
            symptomRepository.getAllSymptoms().collect {
                _allSymptoms = it
                uiState = uiState.copy(
                    searchState = uiState.searchState.copy(results = it)
                )
            }
        }
    }

    abstract suspend fun submit()

    fun addSymptomWithSeverity() {
        if (canAddSelectedSymptomToLog()) {
            _selectedSymptoms.add(uiState.toSymptomWithSeverity())

            uiState = uiState.copy(
                selectedSymptoms = _selectedSymptoms
            )
            clearSearchAndSeverity()
        }
    }

    fun removeSymptom(symptomsWithSeverity: SymptomWithSeverity) {
        _selectedSymptoms.remove(symptomsWithSeverity)

        uiState = uiState.copy(
            selectedSymptoms = _selectedSymptoms
        )
    }

    fun updateSelectedSearchSymptom(symptom: Symptom) {
        uiState = uiState.copy(
            searchState = SearchState(
                input = symptom.name,
                selectedSymptom = symptom,
                results = getSearchResults(symptom.name),
                canCreateNewSymptom = false,
            )
        )
    }

    fun updateSearchInput(input: String) {
        uiState = uiState.copy(
            searchState = SearchState(
                input = input,
                selectedSymptom = null,
                results = getSearchResults(input),
                canCreateNewSymptom = canCreateNewSymptomFromInput(input),
            )
        )
    }

    fun clearSearchAndSeverity() {
        uiState = uiState.copy(
            searchState = SearchState(
                input = "",
                selectedSymptom = null,
                results = getSearchResults(""),
                canCreateNewSymptom = false,
            ),
            selectedSeverity = null,
        )
    }

    fun updateSelectedSeverity(severity: Severity) {
        uiState = uiState.copy(
            selectedSeverity = severity
        )
    }

    suspend fun createNewSymptomFromInput() {
        if (uiState.searchState.isInputValid()) {
            val symptom = uiState.searchState.toSymptom()
            val id = symptomRepository.insertSymptom(symptom)

            updateSelectedSearchSymptom(symptom.copy(id = id))
        }
    }

    fun updateDate(dateInputFields: DateInputFields) {
        uiState = uiState.copy(
            dateTimeInput = uiState.dateTimeInput.copy(
                dateInputFields = dateInputFields
            )
        )
    }

    fun updateTime(timeInputFields: TimeInputFields) {
        uiState = uiState.copy(
            dateTimeInput = uiState.dateTimeInput.copy(
                timeInputFields = timeInputFields
            )
        )
    }

    private fun canAddSelectedSymptomToLog(): Boolean {
        return uiState.searchState.selectedSymptom != null && uiState.selectedSeverity != null && _selectedSymptoms.none { it.symptom == uiState.searchState.selectedSymptom }
    }

    private fun canCreateNewSymptomFromInput(symptomName: String = this.uiState.searchState.input): Boolean {
        return symptomName.isNotBlank() && _allSymptoms.none {
            it.name.equals(symptomName, ignoreCase = true)
        }
    }

    private fun getSearchResults(selectedSymptomName: String = uiState.searchState.input): List<Symptom> {
        return _allSymptoms.filter { symptom ->
            symptom.name.contains(selectedSymptomName, ignoreCase = true)
        }
    }

    protected fun setUiStateWithLog(symptomLog: SymptomLog) {
        uiState = SymptomEntryUiState(log = symptomLog).copy(
            searchState = uiState.searchState.copy(results = _allSymptoms)
        )

        _selectedSymptoms = symptomLog.items.toMutableStateList()
    }
}

data class SymptomEntryUiState(
    val selectedSymptoms: List<SymptomWithSeverity> = listOf(),
    val dateTimeInput: DateTimeInput,
    val searchState: SearchState = SearchState(),
    val selectedSeverity: Severity? = null,
) {
    constructor(calendar: Calendar) : this(
        dateTimeInput = DateTimeInput(calendar = calendar)
    )

    constructor(log: SymptomLog) : this(
        selectedSymptoms = log.items, dateTimeInput = DateTimeInput(date = log.date)
    )

    fun isValid(): Boolean =
        selectedSymptoms.isNotEmpty() && selectedSymptoms.none { symptomWithSeverity -> symptomWithSeverity.symptom.name.isBlank() }

    fun toSymptomWithSeverity() = SymptomWithSeverity(
        symptom = searchState.selectedSymptom!!, severity = selectedSeverity!!
    )

    fun toSymptomLog(id: Long = 0): SymptomLog =
        SymptomLog(
            id = id,
            date = dateTimeInput.toDate(),
            items = selectedSymptoms,
        )
}

data class SearchState(
    val input: String = "",
    val selectedSymptom: Symptom? = null,
    val results: List<Symptom> = listOf(),
    val canCreateNewSymptom: Boolean = false,
) {
    fun isInputValid(): Boolean = input.isNotEmpty()

    fun toSymptom(): Symptom = Symptom(name = input.toFoodItemName())
}
