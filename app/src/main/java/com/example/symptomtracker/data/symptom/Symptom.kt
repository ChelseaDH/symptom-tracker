package com.example.symptomtracker.data.symptom

import androidx.room.*
import java.util.*

@Entity(tableName = "symptom")
data class Symptom(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
)

@Entity(tableName = "symptom_log")
data class SymptomLog(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val date: Date,
)

@Entity(
    tableName = "symptom_log_record",
    primaryKeys = ["symptom_log_id", "symptom_id"],
    foreignKeys = [
        ForeignKey(
            entity = SymptomLog::class,
            parentColumns = ["id"],
            childColumns = ["symptom_log_id"]
        ),
        ForeignKey(entity = Symptom::class, parentColumns = ["id"], childColumns = ["symptom_id"])
    ],
    indices = [
        Index(value = ["symptom_id"])
    ]
)
data class SymptomLogRecord(
    @ColumnInfo(name = "symptom_log_id") val symptomLogId: Long,
    @ColumnInfo(name = "symptom_id") val symptomId: Long,
    val severity: Severity,
)

data class SymptomWithSeverity(
    val symptom: Symptom,
    val severity: Severity,
)

data class SymptomLogWithSymptoms(
    val symptomLog: SymptomLog,
    val symptomsWithSeverity: List<SymptomWithSeverity>,
)