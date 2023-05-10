package com.example.symptomtracker.data.movement

class OfflineMovementRepository(private val movementDao: MovementDao) : MovementRepository {
    override suspend fun insertMovementLog(movementLog: MovementLog) =
        movementDao.insertMovementLog(movementLog = movementLog)
}