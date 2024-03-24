package com.example.symptomtracker.data.movement

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

@Dao
interface MovementDao {
    @Insert
    suspend fun insertMovementLog(movementLog: MovementLog)

    @Query("SELECT * FROM movement_log")
    fun getAllMovementLogs(): Flow<List<MovementLog>>

    @Transaction
    @Query("SELECT * FROM movement_log WHERE date BETWEEN :startDate AND :endDate")
    fun getAllMovementLogsBetweenDates(
        startDate: OffsetDateTime,
        endDate: OffsetDateTime
    ): Flow<List<MovementLog>>
}
