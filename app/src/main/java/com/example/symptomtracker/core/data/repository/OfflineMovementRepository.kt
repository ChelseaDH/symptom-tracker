package com.example.symptomtracker.core.data.repository

import com.example.symptomtracker.core.database.dao.MovementDao
import com.example.symptomtracker.core.database.model.MovementLog
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime
import javax.inject.Inject

class OfflineMovementRepository @Inject constructor(private val movementDao: MovementDao) :
    MovementRepository {
    override suspend fun insertMovementLog(movementLog: MovementLog) =
        movementDao.insertMovementLog(movementLog = movementLog)

    override fun getAllMovementLogs(): Flow<List<MovementLog>> =
        movementDao.getAllMovementLogs()

    override fun getAllMovementLogsBetweenDates(
        startDate: OffsetDateTime,
        endDate: OffsetDateTime
    ): Flow<List<MovementLog>> = movementDao.getAllMovementLogsBetweenDates(startDate, endDate)

    override suspend fun getMovementLog(id: Long): Flow<MovementLog?> =
        movementDao.getMovementLog(id)

    override suspend fun deleteLog(movementLog: MovementLog) =
        movementDao.deleteLog(movementLog)
}
