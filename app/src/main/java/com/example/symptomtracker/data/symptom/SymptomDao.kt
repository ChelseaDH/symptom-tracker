package com.example.symptomtracker.data.symptom

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SymptomDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSymptom(symptom: Symptom): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSymptoms(symptoms: List<Symptom>): Array<Long>

    @Insert
    fun insertSymptomLog(symptomLog: SymptomLog): Long

    @Insert
    suspend fun insertSymptomLogRecord(symptomLogRecord: SymptomLogRecord)

    @Transaction
    suspend fun insertSymptomLogWithSymptoms(symptomLogWithSymptoms: SymptomLogWithSymptoms) {
        val symptomLogId = insertSymptomLog(symptomLogWithSymptoms.symptomLog)

        symptomLogWithSymptoms.symptomsWithSeverity.forEach {
            insertSymptomLogRecord(symptomLogRecord = SymptomLogRecord(
                symptomLogId = symptomLogId,
                symptomId = insertSymptom(it.symptom),
                severity = it.severity
            ))
        }
    }

    @Query("SELECT * FROM symptom ORDER BY name ASC")
    fun getAllSymptoms(): Flow<List<Symptom>>

    @Transaction
    @Query(
        "SELECT * FROM symptom_log sl " +
                "JOIN symptom_log_record slr ON slr.symptom_log_id = sl.id " +
                "JOIN symptom s ON s.id = slr.symptom_id"
    )
    fun getAllSymptomLogs(): Flow<Map<SymptomLog, List<Symptom>>>
}