package com.example.symptomtracker.feature.symptom

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.core.designsystem.component.DateTimeInput
import com.example.symptomtracker.core.designsystem.component.TextInput
import com.example.symptomtracker.core.designsystem.component.TextValidationError
import com.example.symptomtracker.core.domain.model.Severity
import com.example.symptomtracker.core.domain.model.Symptom
import com.example.symptomtracker.core.domain.model.SymptomLog
import com.example.symptomtracker.core.domain.model.SymptomWithSeverity
import com.example.symptomtracker.core.domain.repository.SymptomRepository
import com.example.symptomtracker.core.util.getPrioritisedSearchResults
import com.example.symptomtracker.core.util.toFoodItemName
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

abstract class AbstractSymptomEntryViewModel(private val symptomRepository: SymptomRepository) :
    ViewModel() {
    var uiState by mutableStateOf(SymptomEntryUiState())
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

    abstract fun submit()

    fun handleEvent(event: SymptomEntryEvent) {
        when (event) {
            is SymptomEntryEvent.AddSymptomWithSeverity -> addSymptomWithSeverity()
            is SymptomEntryEvent.RemoveSymptom -> removeSymptom(symptomsWithSeverity = event.symptomWithSeverity)
            is SymptomEntryEvent.UpdateSelectedSearchSymptom -> updateSelectedSearchSymptom(symptom = event.symptom)
            is SymptomEntryEvent.UpdateSearchInput -> updateSearchInput(input = event.input)
            is SymptomEntryEvent.ClearSearchAndSeverity -> clearSearchAndSeverity()
            is SymptomEntryEvent.UpdateSelectedSeverity -> updateSelectedSeverity(severity = event.severity)
            is SymptomEntryEvent.CreateNewSymptomFromInput -> createNewSymptomFromInput()
            is SymptomEntryEvent.UpdateDate -> updateDate(date = event.date)
            is SymptomEntryEvent.UpdateTime -> updateTime(time = event.time)
            is SymptomEntryEvent.Submit -> submit()
        }
    }

    private fun addSymptomWithSeverity() {
        if (canAddSelectedSymptomToLog()) {
            _selectedSymptoms.add(uiState.toSymptomWithSeverity())

            uiState = uiState.copy(
                selectedSymptoms = _selectedSymptoms
            )
            clearSearchAndSeverity()
        }
    }

    private fun removeSymptom(symptomsWithSeverity: SymptomWithSeverity) {
        _selectedSymptoms.remove(symptomsWithSeverity)

        uiState = uiState.copy(
            selectedSymptoms = _selectedSymptoms
        )
    }

    private fun updateSelectedSearchSymptom(symptom: Symptom) {
        uiState = uiState.copy(
            searchState = SearchState(
                input = TextInput(value = symptom.name),
                selectedSymptom = symptom,
                results = getSearchResults(symptom.name),
                canCreateNewSymptom = false,
            )
        )
    }

    private fun updateSearchInput(input: String) {
        uiState = uiState.copy(
            searchState = SearchState(
                input = TextInput(value = input),
                selectedSymptom = null,
                results = getSearchResults(input),
                canCreateNewSymptom = canCreateNewSymptomFromInput(input),
            )
        )
    }

    private fun clearSearchAndSeverity() {
        uiState = uiState.copy(
            searchState = SearchState(
                input = TextInput(),
                selectedSymptom = null,
                results = getSearchResults(""),
                canCreateNewSymptom = false,
            ),
            selectedSeverity = null,
        )
    }

    private fun updateSelectedSeverity(severity: Severity) {
        uiState = uiState.copy(
            selectedSeverity = severity
        )
    }

    private fun createNewSymptomFromInput() {
        val validationError =
            uiState.searchState.input.findValidationError(errors = listOf(TextValidationError.BLANK))

        if (validationError == null) {
            val symptom = uiState.searchState.toSymptom()

            viewModelScope.launch {
                val id = symptomRepository.insertSymptom(symptom)
                updateSelectedSearchSymptom(symptom.copy(id = id))
            }
        } else {
            uiState = uiState.copy(
                searchState = uiState.searchState.copy(
                    input = uiState.searchState.input.copy(validationError = validationError)
                )
            )
        }
    }

    private fun updateDate(date: LocalDate) {
        uiState = uiState.copy(
            dateTimeInput = uiState.dateTimeInput.copy(
                date = date
            )
        )
    }

    private fun updateTime(time: LocalTime) {
        uiState = uiState.copy(
            dateTimeInput = uiState.dateTimeInput.copy(
                time = time
            )
        )
    }

    private fun canAddSelectedSymptomToLog(): Boolean {
        return uiState.searchState.selectedSymptom != null && uiState.selectedSeverity != null && _selectedSymptoms.none { it.symptom == uiState.searchState.selectedSymptom }
    }

    private fun canCreateNewSymptomFromInput(symptomName: String = this.uiState.searchState.input.value): Boolean {
        return symptomName.isNotBlank() && _allSymptoms.none {
            it.name.equals(symptomName, ignoreCase = true)
        }
    }

    private fun getSearchResults(selectedSymptomName: String = uiState.searchState.input.value): List<Symptom> =
        getPrioritisedSearchResults(
            searchTerm = selectedSymptomName,
            items = _allSymptoms,
            getItemComparisonString = { it.name },
        )

    protected fun setUiStateWithLog(symptomLog: SymptomLog) {
        uiState = SymptomEntryUiState(log = symptomLog).copy(
            searchState = uiState.searchState.copy(results = _allSymptoms)
        )

        _selectedSymptoms = symptomLog.items.toMutableStateList()
    }

    protected fun setUiStateWithArgs(date: String?) {
        date?.let {
            uiState = uiState.copy(
                dateTimeInput = uiState.dateTimeInput.copy(
                    date = LocalDate.parse(it)
                )
            )
        }
    }
}

data class SymptomEntryUiState(
    val selectedSymptoms: List<SymptomWithSeverity> = listOf(),
    val dateTimeInput: DateTimeInput = DateTimeInput(),
    val searchState: SearchState = SearchState(),
    val selectedSeverity: Severity? = null,
) {
    constructor(log: SymptomLog) : this(
        selectedSymptoms = log.items, dateTimeInput = DateTimeInput(date = log.date)
    )

    fun isValid(): Boolean =
        selectedSymptoms.isNotEmpty() && selectedSymptoms.none { symptomWithSeverity -> symptomWithSeverity.symptom.name.isBlank() }

    fun toSymptomWithSeverity() = SymptomWithSeverity(
        symptom = searchState.selectedSymptom!!, severity = selectedSeverity!!
    )

    fun toSymptomLog(id: Long = 0): SymptomLog = SymptomLog(
        id = id,
        date = dateTimeInput.toDate(),
        items = selectedSymptoms,
    )
}

data class SearchState(
    val input: TextInput = TextInput(),
    val selectedSymptom: Symptom? = null,
    val results: List<Symptom> = listOf(),
    val canCreateNewSymptom: Boolean = false,
) {
    fun toSymptom(): Symptom = Symptom(name = input.value.toFoodItemName())
}

sealed interface SymptomEntryEvent {
    data object AddSymptomWithSeverity : SymptomEntryEvent
    data class RemoveSymptom(val symptomWithSeverity: SymptomWithSeverity) : SymptomEntryEvent
    data class UpdateSelectedSearchSymptom(val symptom: Symptom) : SymptomEntryEvent
    data class UpdateSearchInput(val input: String) : SymptomEntryEvent
    data object ClearSearchAndSeverity : SymptomEntryEvent
    data class UpdateSelectedSeverity(val severity: Severity) : SymptomEntryEvent
    data object CreateNewSymptomFromInput : SymptomEntryEvent
    data class UpdateDate(val date: LocalDate) : SymptomEntryEvent
    data class UpdateTime(val time: LocalTime) : SymptomEntryEvent
    data object Submit : SymptomEntryEvent
}
