package com.example.symptomtracker.core.testing.repository

import com.example.symptomtracker.core.data.repository.MovementRepository
import com.example.symptomtracker.core.model.MovementLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime

class TestMovementRepository : MovementRepository {
    private val movementLogsFlow: MutableSharedFlow<List<MovementLog>> =
        MutableSharedFlow(replay = 1)

    override suspend fun insertMovementLog(movementLog: MovementLog) {}

    override fun getAllMovementLogs(): Flow<List<MovementLog>> = movementLogsFlow

    override fun getAllMovementLogsBetweenDates(
        startDate: OffsetDateTime,
        endDate: OffsetDateTime
    ): Flow<List<MovementLog>> =
        movementLogsFlow.map { movementLogs ->
            movementLogs.filter {
                it.date.isAfter(startDate) && it.date.isBefore(
                    endDate
                )
            }
        }

    override fun getMovementLogById(id: Long): Flow<MovementLog?> =
        movementLogsFlow.map { movementLogs -> movementLogs.find { it.id == id } }

    override suspend fun deleteLog(movementLog: MovementLog) {}

    override suspend fun updateLog(movementLog: MovementLog) {}

    /**
     * A test-only API to allow controlling the list of movement logs from tests.
     */
    fun sendMovementLogs(topics: List<MovementLog>) {
        movementLogsFlow.tryEmit(topics)
    }
}
