package com.example.symptomtracker.core.model

/**
 * External data layer representation of a symptom with it's associated severity.
 */
data class SymptomWithSeverity(
    val symptom: Symptom,
    val severity: Severity,
)

fun SymptomWithSeverity.getDisplayString(): String = "${symptom.name} (${severity.displayName})"
