package com.example.symptomtracker.feature.mealie

import android.webkit.URLUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.symptomtracker.core.data.repository.SettingsRepository
import com.example.symptomtracker.core.domain.MealieCredentialsValidation
import com.example.symptomtracker.core.domain.ValidateMealieCredentialsUseCase
import com.example.symptomtracker.core.model.TextInput
import com.example.symptomtracker.core.model.TextValidationError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MealieSettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val validateMealieCredentials: ValidateMealieCredentialsUseCase,
) : ViewModel() {
    val uiState = MutableStateFlow<MealieSettingsUiState>(MealieSettingsUiState.Loading)

    private val _saveResult = MutableStateFlow<Boolean?>(null)
    val saveResult: StateFlow<Boolean?> get() = _saveResult

    init {
        viewModelScope.launch {
            settingsRepository.getMealieSettings().collect { settings ->
                uiState.value = MealieSettingsUiState.Success(
                    isEnabled = settings.enabled,
                    baseUrl = TextInput(value = settings.baseUrl),
                    apiToken = TextInput(value = settings.apiToken),
                )
            }
        }
    }

    fun updateIsEnabled(isEnabled: Boolean) {
        (uiState.value as MealieSettingsUiState.Success).let { state ->
            uiState.value = state.copy(isEnabled = isEnabled)
        }
    }

    fun updateBaseUrl(baseUrl: String) {
        uiState.value = (uiState.value as MealieSettingsUiState.Success).copy(
            baseUrl = TextInput(value = baseUrl)
        )
    }

    fun updateApiToken(apiToken: String) {
        uiState.value = (uiState.value as MealieSettingsUiState.Success).copy(
            apiToken = TextInput(value = apiToken)
        )
    }

    fun onCheckCredentials() {
        viewModelScope.launch {
            validateInput()
        }
    }

    fun onSave() {
        viewModelScope.launch {
            val isValid = isStateValidForSave()
            if (isValid) {
                (uiState.value as MealieSettingsUiState.Success).let { state ->
                    if (state.isEnabled) {
                        settingsRepository.enableMealieIntegration(
                            baseUrl = state.baseUrl.value,
                            apiToken = state.apiToken.value
                        )
                    } else {
                        settingsRepository.disableMealieIntegration()
                    }
                }
            }
            _saveResult.value = isValid
        }
    }

    private suspend fun validateInput() {
        (uiState.value as MealieSettingsUiState.Success).let { state ->
            val baseUrlValidationError = state.baseUrl.findValidationError(
                errors = listOf(
                    TextValidationError.BLANK, TextValidationError.INVALID
                ),
                validityCheck = { URLUtil.isValidUrl(it) },
            )
            val apiTokenValidationError = state.apiToken.findValidationError(
                errors = listOf(TextValidationError.BLANK)
            )

            if (baseUrlValidationError != null || apiTokenValidationError != null) {
                uiState.value = state.copy(
                    baseUrl = state.baseUrl.copy(
                        validationError = baseUrlValidationError
                    ), apiToken = state.apiToken.copy(
                        validationError = apiTokenValidationError
                    )
                )

                return
            }

            val credentialsValidation = validateMealieCredentials(
                baseUrl = state.baseUrl.value, apiToken = state.apiToken.value
            )
            uiState.value = state.copy(
                credentialsCheckResult = credentialsValidation
            )
        }
    }

    private suspend fun isStateValidForSave(): Boolean {
        return when (val state = uiState.value) {
            MealieSettingsUiState.Loading -> false
            is MealieSettingsUiState.Success -> {
                if (!state.isEnabled) return true

                validateInput()
                (uiState.value as? MealieSettingsUiState.Success)?.let { newState ->
                    newState.baseUrl.validationError == null && newState.apiToken.validationError == null && newState.credentialsCheckResult is MealieCredentialsValidation.Success
                } ?: false
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
