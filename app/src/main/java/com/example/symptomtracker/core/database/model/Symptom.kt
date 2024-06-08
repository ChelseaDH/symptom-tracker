package com.example.symptomtracker.core.database.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.symptomtracker.core.model.Severity
import com.example.symptomtracker.core.model.SymptomLogWithSymptoms
import com.example.symptomtracker.core.model.SymptomWithSeverity
import java.time.OffsetDateTime

@Entity(tableName = "symptom")
data class Symptom(
    @PrimaryKey(autoGenerate = true) val symptomId: Long,
    val name: String,
)

@Entity(tableName = "symptom_log")
data class SymptomLog(
    @PrimaryKey(autoGenerate = true) val symptomLogId: Long,
    val date: OffsetDateTime,
)

@Entity(
    tableName = "symptom_log_record",
    indices = [Index(value = ["symptomId"])],
    primaryKeys = ["symptomLogId", "symptomId"],
    foreignKeys = [ForeignKey(
        entity = SymptomLog::class,
        parentColumns = ["symptomLogId"],
        childColumns = ["symptomLogId"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Symptom::class,
        parentColumns = ["symptomId"],
        childColumns = ["symptomId"],
        onDelete = ForeignKey.RESTRICT
    )]
)
data class SymptomLogRecord(
    val symptomLogId: Long,
    val symptomId: Long,
    val severity: Severity,
)

data class SymptomLogRecordWithSymptom(
    @Embedded val symptomLogRecord: SymptomLogRecord, @Relation(
        parentColumn = "symptomId", entityColumn = "symptomId"
    ) val symptom: Symptom
)

data class SymptomLogWithLinkedRecords(
    @Embedded val symptomLog: SymptomLog, @Relation(
        entity = SymptomLogRecord::class,
        parentColumn = "symptomLogId",
        entityColumn = "symptomLogId"
    ) val symptomLogRecords: List<SymptomLogRecordWithSymptom>
)

fun SymptomLogWithLinkedRecords.asExternalModel(): SymptomLogWithSymptoms =
    SymptomLogWithSymptoms(log = symptomLog, items = symptomLogRecords.map {
        SymptomWithSeverity(
            symptom = it.symptom,
            severity = it.symptomLogRecord.severity,
        )
    })
