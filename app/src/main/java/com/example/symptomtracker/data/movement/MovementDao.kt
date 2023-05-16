package com.example.symptomtracker.data.movement

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MovementDao {
    @Insert
    suspend fun insertMovementLog(movementLog: MovementLog)

    @Query("SELECT * FROM movement_log")
    fun getAllMovementLogs(): Flow<List<MovementLog>>
}