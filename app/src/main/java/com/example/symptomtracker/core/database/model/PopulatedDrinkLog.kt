package com.example.symptomtracker.core.database.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.symptomtracker.core.domain.model.DrinkLog

/**
 * Represents a fully populated drink log.
 */
data class PopulatedDrinkLog(
    @Embedded val log: DrinkLogEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = DrinkLogItemCrossRef::class,
            parentColumn = "drinkLogId",
            entityColumn = "itemId",
        ),
    ) val drinkItemEntities: List<DrinkItemEntity>,
)

fun PopulatedDrinkLog.asExternalModel(): DrinkLog = DrinkLog(
    id = log.id, date = log.date, items = drinkItemEntities.map { it.asExternalModel() })
