package com.example.symptomtracker.core.data.repository

import com.example.symptomtracker.core.data.model.asEntity
import com.example.symptomtracker.core.database.dao.MovementDao
import com.example.symptomtracker.core.database.model.MovementLogEntity
import com.example.symptomtracker.core.database.model.asExternalModel
import com.example.symptomtracker.core.model.MovementLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime
import javax.inject.Inject

class OfflineMovementRepository @Inject constructor(private val movementDao: MovementDao) :
    MovementRepository {
    override suspend fun insertMovementLog(movementLog: MovementLog) =
        movementDao.insertMovementLog(movementLogEntity = movementLog.asEntity())

    override fun getAllMovementLogs(): Flow<List<MovementLog>> =
        movementDao.getAllMovementLogs().map { it.map(MovementLogEntity::asExternalModel) }

    override fun getAllMovementLogsBetweenDates(
        startDate: OffsetDateTime, endDate: OffsetDateTime
    ): Flow<List<MovementLog>> = movementDao.getAllMovementLogsBetweenDates(startDate, endDate)
        .map { it.map(MovementLogEntity::asExternalModel) }

    override suspend fun getMovementLogById(id: Long): Flow<MovementLog?> =
        movementDao.getMovementLog(id).map { it?.asExternalModel() }

    override suspend fun deleteLog(movementLog: MovementLog) =
        movementDao.deleteLog(movementLogEntity = movementLog.asEntity())

    override suspend fun updateLog(movementLog: MovementLog) =
        movementDao.updateLog(movementLogEntity = movementLog.asEntity())
}
