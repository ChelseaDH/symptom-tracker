package com.example.symptomtracker.data.movement

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.symptomtracker.data.Log
import java.time.OffsetDateTime

@Entity(tableName = "movement_log")
data class MovementLog(
    @PrimaryKey(autoGenerate = true) val movementLogId: Long,
    private val date: OffsetDateTime,
    val stoolType: StoolType,
) : Log {
    override fun getDate(): OffsetDateTime {
        return date
    }
}
