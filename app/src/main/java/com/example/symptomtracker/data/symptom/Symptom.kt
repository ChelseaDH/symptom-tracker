package com.example.symptomtracker.data.symptom

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.symptomtracker.data.Log
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
    primaryKeys = ["symptomLogId", "symptomId"],
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

data class SymptomLogWithSymptomsAndSeverity(
    val log: SymptomLog,
    val items: List<SymptomWithSeverity>,
)

data class SymptomLogWithSymptoms(
    @Embedded val log: SymptomLog,
    @Relation(
        parentColumn = "symptomLogId",
        entityColumn = "symptomId",
        associateBy = Junction(SymptomLogRecord::class)
    )
    val items: List<Symptom>
) : Log {
    override fun getDate(): OffsetDateTime = log.date
}
