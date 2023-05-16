package com.example.symptomtracker.data.movement

import kotlinx.coroutines.flow.Flow

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
}