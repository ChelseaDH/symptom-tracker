package com.example.symptomtracker.data.movement

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

@Entity(tableName = "movement_log")
data class MovementLog(
    @PrimaryKey(autoGenerate = true) val symptomLogId: Long,
    val date: OffsetDateTime,
    val stoolType: StoolType,
)