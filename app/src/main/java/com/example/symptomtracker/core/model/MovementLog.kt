package com.example.symptomtracker.core.model

import java.time.OffsetDateTime

/**
 * External data layer representation of a movement log.
 */
data class MovementLog(
    val id: Long,
    private val date: OffsetDateTime,
    val stoolType: StoolType,
) : Log {
    override fun getDate(): OffsetDateTime = date
}
