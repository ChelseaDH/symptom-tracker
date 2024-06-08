package com.example.symptomtracker.core.model

import com.example.symptomtracker.core.database.model.Symptom
import com.example.symptomtracker.core.database.model.SymptomLog
import java.time.OffsetDateTime

data class SymptomWithSeverity(
    val symptom: Symptom,
    val severity: Severity,
)

fun SymptomWithSeverity.getDisplayString(): String = "${symptom.name} (${severity.displayName})"

data class SymptomLogWithSymptoms(
    val log: SymptomLog,
    val items: List<SymptomWithSeverity>,
) : Log {
    override fun getDate(): OffsetDateTime = log.date
}
