package com.example.symptomtracker.core.data.model

import com.example.symptomtracker.core.database.model.MovementLogEntity
import com.example.symptomtracker.core.model.MovementLog

fun MovementLog.asEntity(): MovementLogEntity =
    MovementLogEntity(id = id, date = getDate(), stoolType = stoolType)
