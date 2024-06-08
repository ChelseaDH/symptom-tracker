package com.example.symptomtracker.core.data.model

import com.example.symptomtracker.core.database.model.FoodLogEntity
import com.example.symptomtracker.core.database.model.PopulatedFoodLog
import com.example.symptomtracker.core.model.FoodLog

fun FoodLog.asEntity(): PopulatedFoodLog = PopulatedFoodLog(
    log = asFoodLogEntity(),
    foodItemEntities = items.map { it.asEntity() }
)

fun FoodLog.asFoodLogEntity(): FoodLogEntity = FoodLogEntity(id = id, date = getDate())
