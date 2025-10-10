package com.example.symptomtracker.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

/**
 * Defines a drink log that a user may record.
 * It has a many to many relationship with [DrinkItemEntity].
 */
@Entity(tableName = "drink_log")
data class DrinkLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val date: OffsetDateTime,
)
