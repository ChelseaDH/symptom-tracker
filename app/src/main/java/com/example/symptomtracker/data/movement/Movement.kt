package com.example.symptomtracker.data.movement

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class MovementLog(
    @PrimaryKey(autoGenerate = true) val symptomLogId: Long,
    val date: Date,
    val stoolType: StoolType,
)