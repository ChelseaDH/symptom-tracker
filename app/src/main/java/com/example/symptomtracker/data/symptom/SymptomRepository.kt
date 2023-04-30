package com.example.symptomtracker.data.symptom

/**
 * Repository that provides insert of [Symptom] from a given data source.
 */
interface SymptomRepository {
    /**
     * Insert symptom in the data source
     */
    suspend fun insertSymptom(symptom: Symptom)
}