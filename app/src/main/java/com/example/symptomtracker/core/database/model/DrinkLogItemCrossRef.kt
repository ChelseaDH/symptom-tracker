package com.example.symptomtracker.core.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Cross reference for many to many relationship between [DrinkLogEntity] and [DrinkItemEntity].
 */
@Entity(
    tableName = "drink_log_item",
    indices = [Index("itemId")],
    primaryKeys = ["drinkLogId", "itemId"],
    foreignKeys = [
        ForeignKey(
            entity = DrinkLogEntity::class,
            parentColumns = ["id"],
            childColumns = ["drinkLogId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = DrinkItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ]
)
data class DrinkLogItemCrossRef(
    val drinkLogId: Long,
    val itemId: Long,
)
