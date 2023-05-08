package com.example.symptomtracker.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.symptomtracker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> OutlinedTextFieldWithDropdown(
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
                    Text(text = stringResource(id = textLabelId),
                        color = MaterialTheme.colorScheme.onPrimaryContainer)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { onClearInput() }) {
                    Icon(imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(id = R.string.clear_input_cd))
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
                Divider()
            }
            if (canCreateOption) {
                DropdownMenuItem(text = { Text(text = textValue) },
                    onClick = { onCreateOption() },
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