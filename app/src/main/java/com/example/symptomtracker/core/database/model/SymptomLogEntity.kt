package com.example.symptomtracker.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

/**
 * Defines a symptom log that a user may record.
 * It has a many to many relationship with [SymptomEntity].
 */
@Entity(tableName = "symptom_log")
data class SymptomLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val date: OffsetDateTime,
)
