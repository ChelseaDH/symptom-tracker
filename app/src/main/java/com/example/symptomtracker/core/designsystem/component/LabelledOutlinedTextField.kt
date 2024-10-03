package com.example.symptomtracker.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.example.symptomtracker.R
import com.example.symptomtracker.core.designsystem.SymptomTrackerTheme
import com.example.symptomtracker.core.ui.TextInputProvider

@Composable
fun LabelledOutlinedTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp),
            style = MaterialTheme.typography.labelLarge,
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            readOnly = readOnly,
            trailingIcon = trailingIcon,
            supportingText = supportingText,
            isError = isError,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
        )
    }
}

@Composable
fun LabelledOutlinedTextInputField(
    label: String,
    input: TextInput,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
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

    LabelledOutlinedTextField(
        label = label,
        value = input.value,
        onValueChange = onValueChange,
        modifier = modifier,
        trailingIcon = trailingIcon,
        supportingText = errorText,
        isError = errorText != null,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> LabelledOutlinedTextInputFieldWithDropdown(
    label: String,
    value: TextInput,
    availableOptions: List<T>,
    canCreateOption: Boolean,
    onValueChange: (String) -> Unit,
    getOptionDisplayName: (T) -> String,
    onCreateOption: () -> Unit,
    onClearInput: () -> Unit,
    onChosenOptionUpdated: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier,
    ) {
        LabelledOutlinedTextInputField(
            label = label,
            input = value,
            onValueChange = {
                onValueChange(it)
                expanded = true
            },
            modifier = modifier.menuAnchor(),
            trailingIcon = {
                IconButton(onClick = { onClearInput() }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(id = R.string.clear_input_cd)
                    )
                }
            }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.exposedDropdownSize(true)
        ) {
            if (availableOptions.isNotEmpty()) {
                for (availableItem in availableOptions) {
                    DropdownMenuItem(text = { Text(text = getOptionDisplayName(availableItem)) },
                        onClick = {
                            expanded = false
                            onChosenOptionUpdated(availableItem)
                        })
                }

                if (canCreateOption) HorizontalDivider()
            }
            if (canCreateOption) {
                DropdownMenuItem(text = { Text(text = value.value) },
                    onClick = {
                        expanded = false
                        onCreateOption()
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.dropdown_create_cd)
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabelledOutlinedReadOnlyDropdown(
    label: String,
    value: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    dropdownMenuContent: @Composable (ColumnScope.() -> Unit),
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = modifier,
    ) {
        LabelledOutlinedTextField(
            label = label,
            value = value,
            onValueChange = {},
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier.exposedDropdownSize(true)
        ) {
            dropdownMenuContent()
        }
    }
}

data class TextInput(
    val value: String = "",
    val validationError: TextValidationError? = null,
) {
    fun findValidationError(
        errors: List<TextValidationError>,
        validityCheck: ((String) -> Boolean)? = null
    ): TextValidationError? {
        return errors.firstOrNull { error ->
            when (error) {
                TextValidationError.BLANK -> value.isBlank()
                TextValidationError.INVALID -> validityCheck?.invoke(value) == false
            }
        }
    }
}

enum class TextValidationError {
    BLANK, INVALID
}

@Preview(showBackground = true)
@Composable
internal fun LabelledOutlinedTextFieldPreview(@PreviewParameter(TextInputProvider::class) input: TextInput) {
    SymptomTrackerTheme {
        LabelledOutlinedTextInputField(
            label = "Name",
            input = input,
            onValueChange = {})
    }
}
