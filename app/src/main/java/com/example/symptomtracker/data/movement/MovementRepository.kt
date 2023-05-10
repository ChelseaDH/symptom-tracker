package com.example.symptomtracker.data.movement

interface MovementRepository {
    suspend fun insertMovementLog(movementLog: MovementLog)
}