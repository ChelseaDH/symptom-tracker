package com.example.symptomtracker.core.model

data class TextInput(
    val value: String,
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
