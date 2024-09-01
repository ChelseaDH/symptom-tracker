package com.example.symptomtracker.core.domain.model

import java.time.OffsetDateTime

sealed class Log {
    abstract val date: OffsetDateTime
}
