package com.example.symptomtracker.core.data.repository

import com.example.symptomtracker.core.database.model.MovementLog
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

/**
 * Repository that provides insert and retrieval of [MovementLog] records from a given data source.
 */
interface MovementRepository {
    /**
     * Insert [MovementLog] record.
     */
    suspend fun insertMovementLog(movementLog: MovementLog)

    /**
     * Retrieves all [MovementLog] records.
     */
    fun getAllMovementLogs(): Flow<List<MovementLog>>

    /**
     * Retrieves all [MovementLog] records between two given dates.
     */
    fun getAllMovementLogsBetweenDates(
        startDate: OffsetDateTime,
        endDate: OffsetDateTime
    ): Flow<List<MovementLog>>

    /**
     * Retrieves a [MovementLog] record with a given ID.
     */
    suspend fun getMovementLog(id: Long): Flow<MovementLog?>

    /**
     * Deletes a [MovementLog].
     */
    suspend fun deleteLog(movementLog: MovementLog)

    /**
     * Updates a [MovementLog] record.
     */
    suspend fun updateLog(movementLog: MovementLog)
}
