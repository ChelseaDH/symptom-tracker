package com.example.symptomtracker.core.ui

import androidx.annotation.StringRes
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
import com.example.symptomtracker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> OutlinedInputTextFieldWithDropdown(
    availableOptions: List<T>,
    getOptionDisplayName: (T) -> String,
    textValue: String,
    onTextValueUpdated: (String) -> Unit,
    canCreateOption: Boolean,
    onCreateOption: () -> Unit,
    onClearInput: () -> Unit,
    onChosenOptionUpdated: (T) -> Unit,
    @StringRes textLabelId: Int?,
    modifier: Modifier = Modifier,
) {
    var selectorExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = selectorExpanded,
        onExpandedChange = { selectorExpanded = !selectorExpanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = textValue,
            onValueChange = {
                onTextValueUpdated(it)
                selectorExpanded = true
            },
            label = {
                if (textLabelId != null)
                    Text(
                        text = stringResource(id = textLabelId),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
            },
            modifier = modifier.menuAnchor(),
            trailingIcon = {
                IconButton(onClick = { onClearInput() }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(id = R.string.clear_input_cd)
                    )
                }
            },
            singleLine = true,
        )
        ExposedDropdownMenu(
            expanded = selectorExpanded,
            onDismissRequest = { selectorExpanded = false },
            modifier = Modifier.exposedDropdownSize(true)
        ) {
            if (availableOptions.isNotEmpty()) {
                for (availableItem in availableOptions) {
                    DropdownMenuItem(text = { Text(text = getOptionDisplayName(availableItem)) },
                        onClick = {
                            onChosenOptionUpdated(availableItem)
                            selectorExpanded = false
                        })
                }
            }
            if (availableOptions.isNotEmpty() && canCreateOption) {
                HorizontalDivider()
            }
            if (canCreateOption) {
                DropdownMenuItem(text = { Text(text = textValue) },
                    onClick = {
                        selectorExpanded = false
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
fun <T> OutlinedReadonlyTextFieldWithDropdown(
    availableOptions: List<T>,
    getOptionDisplayName: (T) -> String,
    chosenOption: T?,
    onChosenOptionUpdated: (T) -> Unit,
    @StringRes textLabelId: Int?,
    modifier: Modifier = Modifier,
) {
    var selectorExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = selectorExpanded,
        onExpandedChange = { selectorExpanded = !selectorExpanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = chosenOption?.let { getOptionDisplayName(it) } ?: "",
            onValueChange = {},
            label = {
                if (textLabelId != null)
                    Text(
                        text = stringResource(id = textLabelId),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
            },
            modifier = modifier.menuAnchor(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = selectorExpanded) },
            singleLine = true,
            readOnly = true,
        )
        ExposedDropdownMenu(
            expanded = selectorExpanded,
            onDismissRequest = { selectorExpanded = false },
            modifier = Modifier.exposedDropdownSize(true)
        ) {
            if (availableOptions.isNotEmpty()) {
                for (availableItem in availableOptions) {
                    DropdownMenuItem(text = { Text(text = getOptionDisplayName(availableItem)) },
                        onClick = {
                            onChosenOptionUpdated(availableItem)
                            selectorExpanded = false
                        })
                }
            }
        }
    }
}
