package com.example.symptomtracker.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.symptomtracker.core.database.model.MovementLogEntity
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

@Dao
interface MovementDao {
    @Insert
    suspend fun insertMovementLog(movementLogEntity: MovementLogEntity)

    @Query("SELECT * FROM movement_log ORDER BY date DESC")
    fun getAllMovementLogs(): Flow<List<MovementLogEntity>>

    @Transaction
    @Query("SELECT * FROM movement_log WHERE date BETWEEN :startDate AND :endDate")
    fun getAllMovementLogsBetweenDates(
        startDate: OffsetDateTime,
        endDate: OffsetDateTime
    ): Flow<List<MovementLogEntity>>

    @Transaction
    @Query("SELECT * FROM movement_log WHERE id = :id")
    fun getMovementLog(id: Long): Flow<MovementLogEntity?>

    @Delete
    suspend fun deleteLog(movementLogEntity: MovementLogEntity)

    @Update
    suspend fun updateLog(movementLogEntity: MovementLogEntity)
}
