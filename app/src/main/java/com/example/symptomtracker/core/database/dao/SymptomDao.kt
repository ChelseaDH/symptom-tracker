package com.example.symptomtracker.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.symptomtracker.core.database.model.Symptom
import com.example.symptomtracker.core.database.model.SymptomLog
import com.example.symptomtracker.core.database.model.SymptomLogRecord
import com.example.symptomtracker.core.database.model.SymptomLogWithLinkedRecords
import com.example.symptomtracker.core.model.SymptomLogWithSymptoms
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

@Dao
interface SymptomDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSymptom(symptom: Symptom): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSymptoms(symptoms: List<Symptom>): Array<Long>

    @Insert
    fun insertSymptomLog(symptomLog: SymptomLog): Long

    @Insert
    suspend fun insertSymptomLogRecord(symptomLogRecord: SymptomLogRecord)

    @Transaction
    suspend fun insertSymptomLogWithSymptoms(symptomLogWithSymptoms: SymptomLogWithSymptoms) {
        val symptomLogId = insertSymptomLog(symptomLogWithSymptoms.log)

        symptomLogWithSymptoms.items.forEach { symptomWithSeverity ->
            insertSymptomLogRecord(
                symptomLogRecord = SymptomLogRecord(
                    symptomLogId = symptomLogId,
                    symptomId = symptomWithSeverity.symptom.symptomId,
                    severity = symptomWithSeverity.severity,
                )
            )
        }
    }

    @Query("SELECT * FROM symptom ORDER BY name ASC")
    fun getAllSymptoms(): Flow<List<Symptom>>

    @Transaction
    @Query("SELECT * FROM symptom_log")
    fun getAllSymptomLogs(): Flow<List<SymptomLogWithLinkedRecords>>

    @Transaction
    @Query("SELECT * FROM symptom_log WHERE date BETWEEN :startDate AND :endDate")
    fun getAllSymptomLogsBetweenDates(
        startDate: OffsetDateTime,
        endDate: OffsetDateTime
    ): Flow<List<SymptomLogWithLinkedRecords>>

    @Transaction
    @Query("SELECT * FROM symptom_log WHERE symptomLogId = :id")
    fun getSymptomLog(id: Long): Flow<SymptomLogWithLinkedRecords?>

    @Delete
    suspend fun deleteLog(symptomLog: SymptomLog)

    @Query("DELETE FROM symptom_log_record WHERE symptomLogId = :id")
    suspend fun deleteAllCrossRefItemsForLogById(id: Long)

    @Update
    suspend fun updateLog(symptomLog: SymptomLog)

    @Transaction
    suspend fun updateLogWithSymptoms(symptomLogWithSymptoms: SymptomLogWithSymptoms) {
        deleteAllCrossRefItemsForLogById(symptomLogWithSymptoms.log.symptomLogId)
        updateLog(symptomLogWithSymptoms.log)

        symptomLogWithSymptoms.items.forEach { item ->
            insertSymptomLogRecord(
                symptomLogRecord = SymptomLogRecord(
                    symptomLogId = symptomLogWithSymptoms.log.symptomLogId,
                    symptomId = item.symptom.symptomId,
                    severity = item.severity,
                )
            )
        }
    }
}
