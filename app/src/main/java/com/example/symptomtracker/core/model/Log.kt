package com.example.symptomtracker.core.model

import java.time.OffsetDateTime

sealed class Log {
    abstract val date: OffsetDateTime
}
