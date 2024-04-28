package com.example.symptomtracker.core.model

import java.time.OffsetDateTime

interface Log {
    fun getDate(): OffsetDateTime
}
