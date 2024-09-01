package com.example.symptomtracker.core.domain.model

import java.time.OffsetDateTime

data class MovementLog(
    val id: Long,
    override val date: OffsetDateTime,
    val stoolType: StoolType,
) : Log()
