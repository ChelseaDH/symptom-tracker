package com.example.symptomtracker.feature.mealie

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.symptomtracker.core.designsystem.SymptomTrackerTheme
import com.example.symptomtracker.core.designsystem.component.ErrorCard
import com.example.symptomtracker.core.designsystem.component.LabelledOutlinedTextInputField
import com.example.symptomtracker.core.designsystem.component.SuccessCard
import com.example.symptomtracker.core.designsystem.component.TextInput
import com.example.symptomtracker.core.domain.usecase.MealieCredentialsValidation
import com.example.symptomtracker.ui.SymptomTrackerTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealieSettingsRoute(
    navigateBack: () -> Unit,
    viewModel: MealieSettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val saveResult by viewModel.saveResult.collectAsState()

    Scaffold(topBar = {
        SymptomTrackerTopAppBar(
            title = stringResource(id = R.string.mealie_settings_title),
            navigateUp = navigateBack,
            actions = {
                TextButton(onClick = { viewModel.handleEvent(MealieSettingsEvent.Save) }) {
                    Text(text = stringResource(R.string.action_save))
                }
            })
    }) { innerPadding ->
        MealieSettingsBody(
            uiState = uiState,
            eventSink = viewModel::handleEvent,
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
    eventSink: (MealieSettingsEvent) -> Unit,
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
                eventSink = eventSink,
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
    eventSink: (MealieSettingsEvent) -> Unit,
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
            Switch(
                checked = isEnabled,
                onCheckedChange = { eventSink(MealieSettingsEvent.UpdateIsEnabled(it)) })
        }

        if (isEnabled) {
            HorizontalDivider()
            LabelledOutlinedTextInputField(
                label = stringResource(id = R.string.mealie_settings_base_url_label),
                input = baseUrl,
                onValueChange = { eventSink(MealieSettingsEvent.UpdateBaseUrl(it)) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.None,
                    keyboardType = KeyboardType.Uri,
                )
            )
            LabelledOutlinedTextInputField(
                label = stringResource(id = R.string.mealie_settings_api_token_label),
                input = apiToken,
                onValueChange = { eventSink(MealieSettingsEvent.UpdateApiToken(it)) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.None,
                )
            )

            FilledTonalButton(
                onClick = { eventSink(MealieSettingsEvent.CheckCredentials) },
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
            eventSink = {},
        )
    }
}
