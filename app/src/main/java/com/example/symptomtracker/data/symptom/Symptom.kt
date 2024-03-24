package com.example.symptomtracker.data.symptom

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

@Entity(tableName = "symptom")
data class Symptom(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
)

@Entity(tableName = "symptom_log")
data class SymptomLog(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val date: OffsetDateTime,
)

@Entity(
    tableName = "symptom_log_record",
    primaryKeys = ["symptomLogId", "symptomId"],
    foreignKeys = [
        ForeignKey(
            entity = SymptomLog::class,
            parentColumns = ["id"],
            childColumns = ["symptomLogId"]
        ),
        ForeignKey(entity = Symptom::class, parentColumns = ["id"], childColumns = ["symptomId"])
    ],
    indices = [
        Index(value = ["symptomId"])
    ]
)
data class SymptomLogRecord(
    val symptomLogId: Long,
    val symptomId: Long,
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