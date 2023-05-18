package com.example.symptomtracker.data.movement

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "movement_log")
data class MovementLog(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val date: Date,
    @ColumnInfo(name = "stool_type") val stoolType: StoolType,
)