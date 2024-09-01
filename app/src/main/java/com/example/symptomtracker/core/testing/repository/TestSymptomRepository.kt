package com.example.symptomtracker.core.testing.repository

import com.example.symptomtracker.core.domain.repository.SymptomRepository
import com.example.symptomtracker.core.domain.model.Symptom
import com.example.symptomtracker.core.domain.model.SymptomLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime

class TestSymptomRepository : SymptomRepository {
    private val symptomsFlow: MutableSharedFlow<List<Symptom>> = MutableSharedFlow(replay = 1)

    private val symptomLogsFlow: MutableSharedFlow<List<SymptomLog>> = MutableSharedFlow(replay = 1)

    override suspend fun insertSymptom(symptom: Symptom): Long = 1

    override suspend fun insertSymptomLog(symptomLog: SymptomLog) {}

    override fun getAllSymptoms(): Flow<List<Symptom>> = symptomsFlow

    override fun getAllSymptomLogs(): Flow<List<SymptomLog>> = symptomLogsFlow

    override fun getAllSymptomLogsBetweenDates(
        startDate: OffsetDateTime, endDate: OffsetDateTime
    ): Flow<List<SymptomLog>> =
        symptomLogsFlow.map { symptomLogs ->
            symptomLogs.filter {
                it.date.isAfter(startDate) && it.date.isBefore(
                    endDate
                )
            }
        }

    override fun getSymptomLogById(id: Long): Flow<SymptomLog?> =
        symptomLogsFlow.map { symptomLogs -> symptomLogs.find { it.id == id } }

    override suspend fun deleteSymptomLog(symptomLog: SymptomLog) {}

    override suspend fun updateSymptomLog(symptomLog: SymptomLog) {}

    /**
     * A test-only API to allow controlling the list of symptoms from tests.
     */
    fun sendSymptoms(topics: List<Symptom>) {
        symptomsFlow.tryEmit(topics)
    }

    /**
     * A test-only API to allow controlling the list of symptom logs from tests.
     */
    fun sendSymptomLogs(topics: List<SymptomLog>) {
        symptomLogsFlow.tryEmit(topics)
    }
}
