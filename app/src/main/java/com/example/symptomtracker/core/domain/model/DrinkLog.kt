package com.example.symptomtracker.core.domain.model

import java.time.OffsetDateTime

data class DrinkLog(
    val id: Long,
    override val date: OffsetDateTime,
    val items: List<DrinkItem>,
) : Log()
