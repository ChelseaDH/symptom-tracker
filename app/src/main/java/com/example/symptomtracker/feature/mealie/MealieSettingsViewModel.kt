package com.example.symptomtracker.feature.mealie

import android.webkit.URLUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.core.designsystem.component.TextInput
import com.example.symptomtracker.core.designsystem.component.TextValidationError
import com.example.symptomtracker.core.domain.repository.SettingsRepository
import com.example.symptomtracker.core.domain.usecase.MealieCredentialsValidation
import com.example.symptomtracker.core.domain.usecase.ValidateMealieCredentialsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MealieSettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val validateMealieCredentials: ValidateMealieCredentialsUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<MealieSettingsUiState>(MealieSettingsUiState.Loading)
    val uiState: StateFlow<MealieSettingsUiState> = _uiState.asStateFlow()

    private val _saveResult = MutableStateFlow<Boolean?>(null)
    val saveResult: StateFlow<Boolean?> = _saveResult.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.getMealieSettings().distinctUntilChanged().collect { settings ->
                _uiState.value = MealieSettingsUiState.Success(
                    isEnabled = settings.enabled,
                    baseUrl = TextInput(value = settings.baseUrl),
                    apiToken = TextInput(value = settings.apiToken),
                )
            }
        }
    }

    fun handleEvent(event: MealieSettingsEvent) {
        when (event) {
            is MealieSettingsEvent.UpdateIsEnabled -> updateIsEnabled(isEnabled = event.isEnabled)
            is MealieSettingsEvent.UpdateBaseUrl -> updateBaseUrl(baseUrl = event.baseUrl)
            is MealieSettingsEvent.UpdateApiToken -> updateApiToken(apiToken = event.apiToken)
            MealieSettingsEvent.CheckCredentials -> onCheckCredentials()
            MealieSettingsEvent.Save -> onSave()
        }
    }

    private fun updateIsEnabled(isEnabled: Boolean) {
        _uiState.update { currentState ->
            (currentState as? MealieSettingsUiState.Success)?.copy(
                isEnabled = isEnabled
            ) ?: currentState
        }
    }

    private fun updateBaseUrl(baseUrl: String) {
        _uiState.update { currentState ->
            (currentState as? MealieSettingsUiState.Success)?.copy(
                baseUrl = TextInput(value = baseUrl)
            ) ?: currentState
        }
    }

    private fun updateApiToken(apiToken: String) {
        _uiState.update { currentState ->
            (currentState as? MealieSettingsUiState.Success)?.copy(
                apiToken = TextInput(value = apiToken)
            ) ?: currentState
        }
    }

    private fun onCheckCredentials() {
        viewModelScope.launch {
            validateInput()
        }
    }

    private fun onSave() {
        viewModelScope.launch {
            val isValid = isStateValidForSave()
            if (isValid) {
                val state = _uiState.value as? MealieSettingsUiState.Success ?: return@launch

                if (state.isEnabled) {
                    settingsRepository.enableMealieIntegration(
                        baseUrl = state.baseUrl.value, apiToken = state.apiToken.value
                    )
                } else {
                    settingsRepository.disableMealieIntegration()
                }
            }
            _saveResult.value = isValid
        }
    }

    private suspend fun validateInput() {
        val state = _uiState.value as? MealieSettingsUiState.Success ?: return

        val baseUrlValidationError = state.baseUrl.findValidationError(
            errors = listOf(TextValidationError.BLANK, TextValidationError.INVALID),
            validityCheck = { URLUtil.isValidUrl(it) },
        )
        val apiTokenValidationError = state.apiToken.findValidationError(
            errors = listOf(TextValidationError.BLANK)
        )

        if (baseUrlValidationError != null || apiTokenValidationError != null) {
            _uiState.update { currentState ->
                (currentState as? MealieSettingsUiState.Success)?.copy(
                    baseUrl = currentState.baseUrl.copy(validationError = baseUrlValidationError),
                    apiToken = currentState.apiToken.copy(validationError = apiTokenValidationError),
                ) ?: currentState
            }
            return
        }

        val credentialsValidation = validateMealieCredentials(
            baseUrl = state.baseUrl.value, apiToken = state.apiToken.value
        )

        _uiState.update { currentState ->
            (currentState as? MealieSettingsUiState.Success)?.copy(
                baseUrl = currentState.baseUrl.copy(validationError = null),
                apiToken = currentState.apiToken.copy(validationError = null),
                credentialsCheckResult = credentialsValidation,
            ) ?: currentState
        }
    }

    private suspend fun isStateValidForSave(): Boolean {
        return when (val state = _uiState.value) {
            MealieSettingsUiState.Loading -> false
            is MealieSettingsUiState.Success -> {
                if (!state.isEnabled) return true

                validateInput()
                val validatedState =
                    _uiState.value as? MealieSettingsUiState.Success ?: return false

                validatedState.baseUrl.validationError == null && validatedState.apiToken.validationError == null && validatedState.credentialsCheckResult is MealieCredentialsValidation.Success
            }
        }
    }
}

sealed interface MealieSettingsUiState {
    data object Loading : MealieSettingsUiState
    data class Success(
        val isEnabled: Boolean,
        val baseUrl: TextInput,
        val apiToken: TextInput,
        val credentialsCheckResult: MealieCredentialsValidation? = null,
    ) : MealieSettingsUiState
}

sealed interface MealieSettingsEvent {
    data class UpdateIsEnabled(val isEnabled: Boolean) : MealieSettingsEvent
    data class UpdateBaseUrl(val baseUrl: String) : MealieSettingsEvent
    data class UpdateApiToken(val apiToken: String) : MealieSettingsEvent
    data object CheckCredentials : MealieSettingsEvent
    data object Save : MealieSettingsEvent
}
