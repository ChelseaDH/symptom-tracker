package com.example.symptomtracker.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.symptomtracker.core.domain.model.MovementLog
import com.example.symptomtracker.core.domain.model.StoolType
import java.time.OffsetDateTime

/**
 * Defines a movement log that a user may record.
 */
@Entity(tableName = "movement_log")
data class MovementLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val date: OffsetDateTime,
    val stoolType: StoolType,
)

fun MovementLogEntity.asExternalModel(): MovementLog =
    MovementLog(id = id, date = date, stoolType = stoolType)
