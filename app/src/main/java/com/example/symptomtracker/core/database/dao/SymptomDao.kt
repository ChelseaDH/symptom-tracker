package com.example.symptomtracker.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.symptomtracker.core.database.model.PopulatedSymptomLog
import com.example.symptomtracker.core.database.model.SymptomEntity
import com.example.symptomtracker.core.database.model.SymptomLogEntity
import com.example.symptomtracker.core.database.model.SymptomLogSymptomCrossRef
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

@Dao
interface SymptomDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSymptom(symptomEntity: SymptomEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSymptoms(symptomEntities: List<SymptomEntity>): Array<Long>

    @Insert
    fun insertSymptomLog(symptomLogEntity: SymptomLogEntity): Long

    @Insert
    suspend fun insertSymptomLogRecord(symptomLogSymptomCrossRef: SymptomLogSymptomCrossRef)

    @Transaction
    suspend fun insertSymptomLogAndAssociatedEntities(populatedSymptomLog: PopulatedSymptomLog) {
        val symptomLogId = insertSymptomLog(populatedSymptomLog.symptomLogEntity)

        populatedSymptomLog.symptomLogRecords.forEach { symptomWithSeverity ->
            insertSymptomLogRecord(
                symptomLogSymptomCrossRef = symptomWithSeverity.symptomLogSymptomCrossRef.copy(
                    symptomLogId = symptomLogId
                )
            )
        }
    }

    @Query("SELECT * FROM symptom ORDER BY name ASC")
    fun getAllSymptoms(): Flow<List<SymptomEntity>>

    @Transaction
    @Query("SELECT * FROM symptom_log")
    fun getAllSymptomLogs(): Flow<List<PopulatedSymptomLog>>

    @Transaction
    @Query("SELECT * FROM symptom_log WHERE date BETWEEN :startDate AND :endDate")
    fun getAllSymptomLogsBetweenDates(
        startDate: OffsetDateTime,
        endDate: OffsetDateTime
    ): Flow<List<PopulatedSymptomLog>>

    @Transaction
    @Query("SELECT * FROM symptom_log WHERE id = :id")
    fun getSymptomLog(id: Long): Flow<PopulatedSymptomLog?>

    @Delete
    suspend fun deleteLog(symptomLogEntity: SymptomLogEntity)

    @Query("DELETE FROM symptom_log_record WHERE symptomLogId = :id")
    suspend fun deleteAllCrossRefItemsForLogById(id: Long)

    @Update
    suspend fun updateLog(symptomLogEntity: SymptomLogEntity)

    @Transaction
    suspend fun updateLogAndAssociatedRecords(populatedSymptomLog: PopulatedSymptomLog) {
        deleteAllCrossRefItemsForLogById(populatedSymptomLog.symptomLogEntity.id)
        updateLog(populatedSymptomLog.symptomLogEntity)

        populatedSymptomLog.symptomLogRecords.forEach { item ->
            insertSymptomLogRecord(
                symptomLogSymptomCrossRef = item.symptomLogSymptomCrossRef
            )
        }
    }
}
