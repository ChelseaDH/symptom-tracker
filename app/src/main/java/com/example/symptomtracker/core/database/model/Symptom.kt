package com.example.symptomtracker.core.database.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.symptomtracker.core.model.Log
import com.example.symptomtracker.core.model.Severity
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
    indices = [
        Index(value = ["symptomId"])
    ],
    primaryKeys = ["symptomLogId", "symptomId"],
    foreignKeys = [
        ForeignKey(
            entity = SymptomLog::class,
            parentColumns = ["symptomLogId"],
            childColumns = ["symptomLogId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Symptom::class,
            parentColumns = ["symptomId"],
            childColumns = ["symptomId"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class SymptomLogRecord(
    val symptomLogId: Long,
    val symptomId: Long,
    val severity: Severity,
)

data class SymptomLogRecordWithSymptom(
    @Embedded val symptomLogRecord: SymptomLogRecord,
    @Relation(
        parentColumn = "symptomId",
        entityColumn = "symptomId"
    )
    val symptom: Symptom
)

data class SymptomLogWithLinkedRecords(
    @Embedded val symptomLog: SymptomLog,
    @Relation(
        entity = SymptomLogRecord::class,
        parentColumn = "symptomLogId",
        entityColumn = "symptomLogId"
    )
    val symptomLogRecords: List<SymptomLogRecordWithSymptom>
) {
    fun toSymptomLogWithSymptomsAndSeverity(): SymptomLogWithSymptomsAndSeverity {
        return SymptomLogWithSymptomsAndSeverity(
            log = symptomLog,
            items = symptomLogRecords.map {
                SymptomWithSeverity(
                    symptom = it.symptom,
                    severity = it.symptomLogRecord.severity,
                )
            }
        )
    }
}

data class SymptomWithSeverity(
    val symptom: Symptom,
    val severity: Severity,
)

data class SymptomLogWithSymptomsAndSeverity(
    val log: SymptomLog,
    val items: List<SymptomWithSeverity>,
) : Log {
    override fun getDate(): OffsetDateTime = log.date
}

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
