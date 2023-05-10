package com.example.symptomtracker.data.movement

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface MovementDao {
    @Insert
    suspend fun insertMovementLog(movementLog: MovementLog)
}