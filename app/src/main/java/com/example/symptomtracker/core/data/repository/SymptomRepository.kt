package com.example.symptomtracker.core.data.repository

import com.example.symptomtracker.core.model.Symptom
import com.example.symptomtracker.core.model.SymptomLog
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

/**
 * Data layer implementation for [Symptom] and [SymptomLog].
 */
interface SymptomRepository {
    /**
     * Inserts a symptom.
     */
    suspend fun insertSymptom(symptom: Symptom): Long

    /**
     * Inserts a symptom log and it's associated symptoms with their severities.
     */
    suspend fun insertSymptomLog(symptomLog: SymptomLog)

    /**
     * Get all symptom records.
     */
    fun getAllSymptoms(): Flow<List<Symptom>>

    /**
     * Get all symptom logs with their associated symptoms with their severities.
     */
    fun getAllSymptomLogs(): Flow<List<SymptomLog>>

    /**
     * Get all symptom logs with their associated symptoms with their severities between two dates.
     */
    fun getAllSymptomLogsBetweenDates(
        startDate: OffsetDateTime,
        endDate: OffsetDateTime
    ): Flow<List<SymptomLog>>

    /**
     * Get a symptom logs with it's associated symptoms with their severities, if it exists.
     */
    fun getSymptomLogById(id: Long): Flow<SymptomLog?>

    /**
     * Deletes a symptom log and the links to it's associated symptoms.
     */
    suspend fun deleteSymptomLog(symptomLog: SymptomLog)

    /**
     * Updates a symptom log and it's associated symptoms and their severities.
     */
    suspend fun updateSymptomLog(symptomLog: SymptomLog)
}
