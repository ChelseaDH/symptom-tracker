package com.example.symptomtracker.core.domain.repository

import com.example.symptomtracker.core.domain.model.MovementLog
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

/**
 * Data layer implementation for [MovementLog].
 */
interface MovementRepository {
    /**
     * Inserts a movement log.
     */
    suspend fun insertMovementLog(movementLog: MovementLog)

    /**
     * Get all movement log records.
     */
    fun getAllMovementLogs(): Flow<List<MovementLog>>

    /**
     * Get all movement logs between teo dates.
     */
    fun getAllMovementLogsBetweenDates(
        startDate: OffsetDateTime,
        endDate: OffsetDateTime
    ): Flow<List<MovementLog>>

    /**
     * Get a movement log, if it exists.
     */
    fun getMovementLogById(id: Long): Flow<MovementLog?>

    /**
     * Deletes a movement log.
     */
    suspend fun deleteLog(movementLog: MovementLog)

    /**
     * Updates a movement log.
     */
    suspend fun updateLog(movementLog: MovementLog)
}
