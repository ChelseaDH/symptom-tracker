package com.example.symptomtracker.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

/**
 * Defines a food log that a user may record.
 * It has a many to many relationship with [FoodItemEntity].
 */
@Entity(tableName = "food_log")
data class FoodLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val date: OffsetDateTime,
)
