package com.example.symptomtracker.core.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Cross reference for many to many relationship between [FoodLogEntity] and [FoodItemEntity].
 */
@Entity(
    tableName = "food_log_item",
    indices = [Index("itemId")],
    primaryKeys = ["foodLogId", "itemId"],
    foreignKeys = [
        ForeignKey(
            entity = FoodLogEntity::class,
            parentColumns = ["id"],
            childColumns = ["foodLogId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = FoodItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.RESTRICT,
        )
    ]
)
data class FoodLogItemCrossRef(
    val foodLogId: Long,
    val itemId: Long,
)
