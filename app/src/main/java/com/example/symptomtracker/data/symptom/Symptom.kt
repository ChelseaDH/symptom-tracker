package com.example.symptomtracker.data.symptom

import androidx.room.*
import java.util.*

@Entity
data class Symptom(
    @PrimaryKey(autoGenerate = true) val symptomId: Long,
    val name: String,
)

@Entity
data class SymptomLog(
    @PrimaryKey(autoGenerate = true) val symptomLogId: Long,
    val date: Date,
)

@Entity(primaryKeys = ["symptomId", "symptomLogId"])
data class SymptomLogRecord(
    val symptomId: Long,
    val symptomLogId: Long,
    val severity: Severity,
)

data class SymptomWithSeverity(
    @Embedded val symptom: Symptom,
    val severity: Severity,
)

data class SymptomLogWithSymptoms(
    @Embedded val symptomLog: SymptomLog,
    @Relation(
        parentColumn = "symptomLogId",
        entityColumn = "symptomId",
        associateBy = Junction(SymptomLogRecord::class)
    )
    val symptomWithSeverities: List<SymptomWithSeverity>,
)