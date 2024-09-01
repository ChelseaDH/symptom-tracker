package com.example.symptomtracker.core.data.model

import com.example.symptomtracker.core.database.model.MovementLogEntity
import com.example.symptomtracker.core.domain.model.MovementLog

fun MovementLog.asEntity(): MovementLogEntity =
    MovementLogEntity(id = id, date = date, stoolType = stoolType)
