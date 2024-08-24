package com.example.symptomtracker.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.example.symptomtracker.R
import com.example.symptomtracker.core.model.TextInput
import com.example.symptomtracker.core.model.TextValidationError

@Composable
fun LabelledOutlinedTextField(
    label: String,
    input: TextInput,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        var errorText: @Composable (() -> Unit)? = null
        if (input.validationError != null) {
            errorText = {
                Text(
                    text = when (input.validationError) {
                        TextValidationError.BLANK -> stringResource(R.string.validation_required)
                        TextValidationError.INVALID -> stringResource(R.string.validation_invalid)
                    }
                )
            }
        }

        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.labelLarge,
        )

        OutlinedTextField(
            value = input.value,
            onValueChange = { onValueChange(it) },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = trailingIcon,
            supportingText = errorText,
            isError = errorText != null,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
        )
    }
}

@Preview(showBackground = true)
@Composable
internal fun LabelledOutlinedTextFieldPreview(@PreviewParameter(TextInputProvider::class) input: TextInput) {
    SymptomTrackerTheme {
        LabelledOutlinedTextField(
            label = "Name",
            input = input,
            onValueChange = {})
    }
}
