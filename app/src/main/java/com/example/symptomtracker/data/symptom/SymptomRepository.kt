package com.example.symptomtracker.data.symptom

import kotlinx.coroutines.flow.Flow

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
     * Insert [SymptomLog] and associated [Symptom] records via the [SymptomLogWithSymptoms] object.
     */
    suspend fun insertSymptomLogWithSymptom(symptomLogWithSymptoms: SymptomLogWithSymptoms)
}