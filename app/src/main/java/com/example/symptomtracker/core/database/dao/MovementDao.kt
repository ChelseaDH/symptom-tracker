package com.example.symptomtracker.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.symptomtracker.core.database.model.MovementLog
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

@Dao
interface MovementDao {
    @Insert
    suspend fun insertMovementLog(movementLog: MovementLog)

    @Query("SELECT * FROM movement_log ORDER BY date DESC")
    fun getAllMovementLogs(): Flow<List<MovementLog>>

    @Transaction
    @Query("SELECT * FROM movement_log WHERE date BETWEEN :startDate AND :endDate")
    fun getAllMovementLogsBetweenDates(
        startDate: OffsetDateTime,
        endDate: OffsetDateTime
    ): Flow<List<MovementLog>>

    @Transaction
    @Query("SELECT * FROM movement_log WHERE movementLogId = :id")
    fun getMovementLog(id: Long): Flow<MovementLog>
}
