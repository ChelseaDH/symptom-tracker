package com.example.symptomtracker.core.data.repository

import com.example.symptomtracker.core.database.model.Symptom
import com.example.symptomtracker.core.database.model.SymptomLog
import com.example.symptomtracker.core.database.model.SymptomLogWithSymptoms
import com.example.symptomtracker.core.database.model.SymptomLogWithSymptomsAndSeverity
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

/**
 * Repository that provides insert of [Symptom] from a given data source.
 */
interface SymptomRepository {
    /**
     * Insert [Symptom].
     */
    fun insertSymptom(symptom: Symptom): Long

    /**
     * Retrieves all [Symptom] records.
     */
    fun getAllSymptomsStream(): Flow<List<Symptom>>

    /**
     * Retrieves all [SymptomLog] records with their associated [Symptom] objects.
     */
    fun getAllSymptomLogs(): Flow<List<SymptomLogWithSymptoms>>

    /**
     * Retrieves all [SymptomLog] records with their associated [Symptom] records between two given dates.
     */
    fun getAllSymptomLogsBetweenDates(
        startDate: OffsetDateTime,
        endDate: OffsetDateTime
    ): Flow<List<SymptomLogWithSymptoms>>

    /**
     * Insert [SymptomLog] and associated [Symptom] records via the [SymptomLogWithSymptomsAndSeverity] object.
     */
    suspend fun insertSymptomLogWithSymptom(symptomLogWithSymptoms: SymptomLogWithSymptomsAndSeverity)
}
