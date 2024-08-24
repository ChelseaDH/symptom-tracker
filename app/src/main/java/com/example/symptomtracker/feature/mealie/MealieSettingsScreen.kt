package com.example.symptomtracker.feature.mealie

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.symptomtracker.R
import com.example.symptomtracker.core.domain.MealieCredentialsValidation
import com.example.symptomtracker.core.model.TextInput
import com.example.symptomtracker.core.ui.ErrorCard
import com.example.symptomtracker.core.ui.LabelledOutlinedTextField
import com.example.symptomtracker.core.ui.SuccessCard
import com.example.symptomtracker.core.ui.SymptomTrackerTheme
import com.example.symptomtracker.ui.SymptomTrackerTopAppBar

@Composable
fun MealieSettingsRoute(
    navigateBack: () -> Unit,
    viewModel: MealieSettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val saveResult by viewModel.saveResult.collectAsState()

    Scaffold(topBar = {
        SymptomTrackerTopAppBar(title = stringResource(id = R.string.mealie_settings_title),
            canNavigateBack = true,
            navigateUp = navigateBack,
            actions = {
                TextButton(onClick = viewModel::onSave) {
                    Text(text = stringResource(R.string.action_save))
                }
            })
    }) { innerPadding ->
        MealieSettingsBody(
            uiState = uiState,
            onIsEnabledToggled = viewModel::updateIsEnabled,
            onBaseUrlUpdated = viewModel::updateBaseUrl,
            onApiTokenUpdated = viewModel::updateApiToken,
            checkCredentials = viewModel::onCheckCredentials,
            modifier = Modifier.padding(innerPadding)
        )
    }

    LaunchedEffect(saveResult) {
        if (saveResult == true) {
            navigateBack()
        }
    }
}

@Composable
internal fun MealieSettingsBody(
    uiState: MealieSettingsUiState,
    onIsEnabledToggled: (Boolean) -> Unit,
    onBaseUrlUpdated: (String) -> Unit,
    onApiTokenUpdated: (String) -> Unit,
    checkCredentials: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        when (uiState) {
            is MealieSettingsUiState.Loading -> CircularProgressIndicator(
                modifier = Modifier.align(
                    Alignment.CenterHorizontally
                )
            )

            is MealieSettingsUiState.Success -> SuccessContent(
                isEnabled = uiState.isEnabled,
                baseUrl = uiState.baseUrl,
                apiToken = uiState.apiToken,
                credentialsCheckResult = uiState.credentialsCheckResult,
                onIsEnabledToggled = onIsEnabledToggled,
                onBaseUrlUpdated = onBaseUrlUpdated,
                onApiTokenUpdated = onApiTokenUpdated,
                checkCredentials = checkCredentials,
            )
        }
    }
}

@Composable
internal fun SuccessContent(
    isEnabled: Boolean,
    baseUrl: TextInput,
    apiToken: TextInput,
    credentialsCheckResult: MealieCredentialsValidation?,
    onIsEnabledToggled: (Boolean) -> Unit,
    onBaseUrlUpdated: (String) -> Unit,
    onApiTokenUpdated: (String) -> Unit,
    checkCredentials: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = stringResource(R.string.mealie_settings_enable_integration_label))
            Switch(checked = isEnabled, onCheckedChange = onIsEnabledToggled)
        }

        if (isEnabled) {
            HorizontalDivider()
            LabelledOutlinedTextField(
                label = stringResource(id = R.string.mealie_settings_base_url_label),
                input = baseUrl,
                onValueChange = onBaseUrlUpdated,
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.None,
                    keyboardType = KeyboardType.Uri,
                )
            )
            LabelledOutlinedTextField(
                label = stringResource(id = R.string.mealie_settings_api_token_label),
                input = apiToken,
                onValueChange = onApiTokenUpdated,
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.None,
                )
            )

            FilledTonalButton(
                onClick = { checkCredentials() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text(text = stringResource(R.string.mealie_settings_check_credentials))
            }

            when (credentialsCheckResult) {
                null -> {}

                is MealieCredentialsValidation.Success -> SuccessCard(
                    message = stringResource(
                        R.string.mealie_settings_check_credentials_success,
                        credentialsCheckResult.username
                    )
                )

                is MealieCredentialsValidation.Error -> ErrorCard(message = stringResource(R.string.mealie_settings_check_credentials_failure))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun MealieSettingsBodyPreview(@PreviewParameter(MealieSettingsStateProvider::class) uiState: MealieSettingsUiState) {
    SymptomTrackerTheme {
        MealieSettingsBody(
            uiState = uiState,
            onIsEnabledToggled = {},
            onBaseUrlUpdated = {},
            onApiTokenUpdated = {},
            checkCredentials = {},
        )
    }
}
