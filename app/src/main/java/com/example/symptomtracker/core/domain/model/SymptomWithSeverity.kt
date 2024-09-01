package com.example.symptomtracker.core.domain.model

data class SymptomWithSeverity(
    val symptom: Symptom,
    val severity: Severity,
)

fun SymptomWithSeverity.getDisplayString(): String = "${symptom.name} (${severity.displayName})"
