package com.example.symptomtracker.data.symptom

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

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
    suspend fun insertSymptomLogWithSymptoms(symptomLogWithSymptoms: SymptomLogWithSymptomsAndSeverity) {
        val symptomLogId = insertSymptomLog(symptomLogWithSymptoms.log)

        symptomLogWithSymptoms.items.forEach {
            insertSymptomLogRecord(
                symptomLogRecord = SymptomLogRecord(
                    symptomLogId = symptomLogId,
                    symptomId = insertSymptom(it.symptom),
                    severity = it.severity
                )
            )
        }
    }

    @Query("SELECT * FROM symptom ORDER BY name ASC")
    fun getAllSymptoms(): Flow<List<Symptom>>

    @Transaction
    @Query(
        "SELECT * FROM symptom_log sl JOIN symptom_log_record slr ON slr.symptomLogId = sl.symptomLogId JOIN symptom s ON s.symptomId = slr.symptomId"
    )
    fun getAllSymptomLogs(): Flow<Map<SymptomLog, List<Symptom>>>

    @Transaction
    @Query("SELECT * FROM symptom_log sl JOIN symptom_log_record slr ON slr.symptomLogId = sl.symptomLogId JOIN symptom s ON s.symptomId = slr.symptomId WHERE sl.date BETWEEN :startDate AND :endDate")
    fun getAllSymptomLogsBetweenDates(
        startDate: OffsetDateTime,
        endDate: OffsetDateTime
    ): Flow<List<SymptomLogWithSymptoms>>
}
