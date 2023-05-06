package com.example.symptomtracker.data.symptom

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SymptomDao {
    @Insert
    suspend fun insertSymptom(symptom: Symptom)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSymptoms(symptoms: List<Symptom>): Array<Long>

    @Insert
    fun insertSymptomLog(symptomLog: SymptomLog): Long

    @Insert
    suspend fun insertSymptomLogCrossRef(symptomLogCrossRef: SymptomLogCrossRef)

    @Transaction
    suspend fun insertSymptomLogWithSymptoms(symptomLogWithSymptoms: SymptomLogWithSymptoms) {
        val symptomLogId = insertSymptomLog(symptomLogWithSymptoms.symptomLog)
        val symptomIds = insertSymptoms(symptomLogWithSymptoms.symptoms)

        symptomIds.forEach { itemId ->
            insertSymptomLogCrossRef(symptomLogCrossRef = SymptomLogCrossRef(
                symptomLogId = symptomLogId,
                symptomId = itemId
            ))
        }
    }

    @Query("SELECT * FROM symptom ORDER BY name ASC")
    fun getAllSymptoms(): Flow<List<Symptom>>
}