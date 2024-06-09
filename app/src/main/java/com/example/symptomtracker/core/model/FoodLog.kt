package com.example.symptomtracker.core.model

import java.time.OffsetDateTime

/**
 * External data layer representation of a food log and it's associated items.
 */
data class FoodLog(
    val id: Long,
    override val date: OffsetDateTime,
    val items: List<FoodItem>,
) : Log()
