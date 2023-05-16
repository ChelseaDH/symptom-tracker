package com.example.symptomtracker.data.movement

import kotlinx.coroutines.flow.Flow

class OfflineMovementRepository(private val movementDao: MovementDao) : MovementRepository {
    override suspend fun insertMovementLog(movementLog: MovementLog) =
        movementDao.insertMovementLog(movementLog = movementLog)

    override fun getAllMovementLogs(): Flow<List<MovementLog>> =
        movementDao.getAllMovementLogs()
}