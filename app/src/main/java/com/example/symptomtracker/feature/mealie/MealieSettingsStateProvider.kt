package com.example.symptomtracker.feature.mealie

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.symptomtracker.core.domain.MealieCredentialsValidation
import com.example.symptomtracker.core.model.TextInput
import com.example.symptomtracker.core.model.TextValidationError

class MealieSettingsStateProvider : PreviewParameterProvider<MealieSettingsUiState> {
    override val values: Sequence<MealieSettingsUiState>
        get() = sequenceOf(
            MealieSettingsUiState.Loading,
            MealieSettingsUiState.Success(
                isEnabled = false,
                baseUrl = TextInput(value = ""),
                apiToken = TextInput(value = "")
            ),
            MealieSettingsUiState.Success(
                isEnabled = true,
                baseUrl = TextInput(value = "   ", validationError = TextValidationError.BLANK),
                apiToken = TextInput(value = "   ", validationError = TextValidationError.BLANK)
            ),
            MealieSettingsUiState.Success(
                isEnabled = true,
                baseUrl = TextInput(
                    value = "not a url",
                    validationError = TextValidationError.INVALID
                ),
                apiToken = TextInput(value = "ieo398kmssjowiw9")
            ),
            MealieSettingsUiState.Success(
                isEnabled = true,
                baseUrl = TextInput(value = "https://fake.mealie.co"),
                apiToken = TextInput(value = "", validationError = TextValidationError.BLANK)
            ),
            MealieSettingsUiState.Success(
                isEnabled = true,
                baseUrl = TextInput(value = "https://fake.mealie.co"),
                apiToken = TextInput(value = "ieo398kmssjowiw9")
            ),
            MealieSettingsUiState.Success(
                isEnabled = true,
                baseUrl = TextInput(value = "https://fake.mealie.co"),
                apiToken = TextInput(value = "ieo398kmssjowiw9"),
                credentialsCheckResult = MealieCredentialsValidation.Success(username = "Jane Doe")
            ),
            MealieSettingsUiState.Success(
                isEnabled = true,
                baseUrl = TextInput(value = "https://fake.mealie.co"),
                apiToken = TextInput(value = "ieo398kmssjowiw9"),
                credentialsCheckResult = MealieCredentialsValidation.Error
            ),
        )
}
