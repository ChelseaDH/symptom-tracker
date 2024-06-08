package com.example.symptomtracker.core.database.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.symptomtracker.core.model.FoodLog

/**
 * Represents a fully populated food log.
 */
data class PopulatedFoodLog(
    @Embedded val log: FoodLogEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = FoodLogItemCrossRef::class,
            parentColumn = "foodLogId",
            entityColumn = "itemId",
        )
    )
    val foodItemEntities: List<FoodItemEntity>,
)

fun PopulatedFoodLog.asExternalModel(): FoodLog = FoodLog(
    id = log.id,
    date = log.date,
    items = foodItemEntities.map { it.asExternalModel() }
)
