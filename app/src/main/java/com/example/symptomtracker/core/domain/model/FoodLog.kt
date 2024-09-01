package com.example.symptomtracker.core.domain.model

import java.time.OffsetDateTime

data class FoodLog(
    val id: Long,
    override val date: OffsetDateTime,
    val items: List<FoodItem>,
) : Log()
