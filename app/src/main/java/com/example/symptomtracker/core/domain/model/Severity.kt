package com.example.symptomtracker.core.domain.model

enum class Severity(val displayName: String) {
    MILD(displayName = "Mild"),
    MODERATE(displayName = "Moderate"),
    SEVERE(displayName = "Severe"),
}
