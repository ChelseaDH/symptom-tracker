package com.example.symptomtracker.core.database.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.symptomtracker.core.model.FoodItem

/**
 * Defines an item that a user may record against a food log.
 * It has a many to many relationship with [FoodLogEntity].
 */
@Entity(
    tableName = "item",
    indices = [Index("name", unique = true)]
)
data class FoodItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
)

fun FoodItemEntity.asExternalModel(): FoodItem = FoodItem(id = id, name = name)
