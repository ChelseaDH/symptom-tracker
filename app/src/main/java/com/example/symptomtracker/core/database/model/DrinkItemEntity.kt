package com.example.symptomtracker.core.database.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.symptomtracker.core.domain.model.DrinkItem

/**
 * Defines an item that a user may record against a drink log.
 * It has a many to many relationship with [DrinkLogEntity].
 */
@Entity(
    tableName = "drink_item",
    indices = [Index("name", unique = true)]
)
data class DrinkItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
)

fun DrinkItemEntity.asExternalModel(): DrinkItem = DrinkItem(id = id, name = name)
