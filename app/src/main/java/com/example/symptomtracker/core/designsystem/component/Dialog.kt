package com.example.symptomtracker.core.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.symptomtracker.R

@Composable
fun Dialog(
    title: String,
    @StringRes confirmButtonText: Int,
    confirmButtonEnabled: Boolean = true,
    icon: @Composable (() -> Unit),
    onSubmit: () -> Unit,
    onClose: () -> Unit,
    content: @Composable (() -> Unit),
) {
    AlertDialog(
        onDismissRequest = onClose,
        confirmButton = {
            TextButton(
                onClick = {
                    onSubmit()
                    onClose()
                },
                enabled = confirmButtonEnabled
            ) {
                Text(text = stringResource(id = confirmButtonText))
            }
        },
        dismissButton = {
            TextButton(onClick = onClose) {
                Text(text = stringResource(id = R.string.action_cancel))
            }
        },
        icon = icon,
        title = { Text(text = title) },
        text = content,
    )
}
