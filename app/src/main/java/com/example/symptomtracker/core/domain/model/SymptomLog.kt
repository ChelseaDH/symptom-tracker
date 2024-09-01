package com.example.symptomtracker.core.domain.model

import java.time.OffsetDateTime

data class SymptomLog(
    val id: Long,
    override val date: OffsetDateTime,
    val items: List<SymptomWithSeverity>,
) : Log()
