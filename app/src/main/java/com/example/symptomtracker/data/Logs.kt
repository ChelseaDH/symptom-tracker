package com.example.symptomtracker.data

import java.time.OffsetDateTime

interface Log {
    fun getDate(): OffsetDateTime
}
