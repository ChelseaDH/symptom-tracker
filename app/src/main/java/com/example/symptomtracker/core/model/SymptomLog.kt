package com.example.symptomtracker.core.model

import java.time.OffsetDateTime

/**
 * External data layer representation of a symptom log and it's associated symptoms with their severities.
 */
data class SymptomLog(
    val id: Long,
    private val date: OffsetDateTime,
    val items: List<SymptomWithSeverity>,
) : Log {
    override fun getDate(): OffsetDateTime = date
}
