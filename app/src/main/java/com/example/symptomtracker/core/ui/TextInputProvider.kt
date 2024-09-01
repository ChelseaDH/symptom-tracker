package com.example.symptomtracker.core.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.symptomtracker.core.designsystem.component.TextInput
import com.example.symptomtracker.core.designsystem.component.TextValidationError

class TextInputProvider : PreviewParameterProvider<TextInput> {
    override val values: Sequence<TextInput>
        get() = sequenceOf(
            TextInput(value = "Textual input"),
            TextInput(value = "   ", validationError = TextValidationError.BLANK),
            TextInput(value = "Gibberish", validationError = TextValidationError.INVALID),
        )
}
