package com.example.symptomtracker.core.data.model

import com.example.symptomtracker.core.database.model.DrinkLogEntity
import com.example.symptomtracker.core.database.model.PopulatedDrinkLog
import com.example.symptomtracker.core.domain.model.DrinkLog

fun DrinkLog.asEntity(): PopulatedDrinkLog = PopulatedDrinkLog(
    log = asDrinkLogEntity(), drinkItemEntities = items.map { it.asEntity() })

fun DrinkLog.asDrinkLogEntity(): DrinkLogEntity = DrinkLogEntity(id = id, date = date)
