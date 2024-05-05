package com.example.symptomtracker.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.symptomtracker.core.database.model.Symptom
import com.example.symptomtracker.core.database.model.SymptomLog
import com.example.symptomtracker.core.database.model.SymptomLogRecord
import com.example.symptomtracker.core.database.model.SymptomLogWithSymptoms
import com.example.symptomtracker.core.database.model.SymptomLogWithSymptomsAndSeverity
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
        "SELECT * FROM symptom_log sl JOIN symptom_log_record slr ON slr.symptomLogId = sl.symptomLogId JOIN symptom s ON s.symptomId = slr.symptomId ORDER BY date DESC"
    )
    fun getAllSymptomLogs(): Flow<List<SymptomLogWithSymptoms>>

    @Transaction
    @Query("SELECT * FROM symptom_log sl JOIN symptom_log_record slr ON slr.symptomLogId = sl.symptomLogId JOIN symptom s ON s.symptomId = slr.symptomId WHERE sl.date BETWEEN :startDate AND :endDate")
    fun getAllSymptomLogsBetweenDates(
        startDate: OffsetDateTime,
        endDate: OffsetDateTime
    ): Flow<List<SymptomLogWithSymptoms>>

    @Transaction
    @Query("SELECT * FROM symptom_log sl JOIN symptom_log_record slr ON slr.symptomLogId = sl.symptomLogId JOIN symptom s ON s.symptomId = slr.symptomId WHERE sl.symptomLogId = :id")
    fun getSymptomLog(id: Long): Flow<SymptomLogWithSymptoms>
}
