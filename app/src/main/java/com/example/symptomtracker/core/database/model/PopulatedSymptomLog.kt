package com.example.symptomtracker.core.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.symptomtracker.core.domain.model.SymptomLog
import com.example.symptomtracker.core.domain.model.SymptomWithSeverity

/**
 * Represents a symptom log cross ref will a fully populated symptom.
 */
data class SymptomLogRecordWithSymptom(
    @Embedded val symptomLogSymptomCrossRef: SymptomLogSymptomCrossRef,
    @Relation(
        parentColumn = "symptomId",
        entityColumn = "id"
    )
    val symptomEntity: SymptomEntity,
)

fun SymptomLogRecordWithSymptom.asExternalModel(): SymptomWithSeverity = SymptomWithSeverity(
    symptom = symptomEntity.asExternalModel(),
    severity = symptomLogSymptomCrossRef.severity,
)

/**
 * Represents a fully populated symptom log.
 */
data class PopulatedSymptomLog(
    @Embedded val symptomLogEntity: SymptomLogEntity,
    @Relation(
        entity = SymptomLogSymptomCrossRef::class,
        parentColumn = "id",
        entityColumn = "symptomLogId",
    )
    val symptomLogRecords: List<SymptomLogRecordWithSymptom>,
)

fun PopulatedSymptomLog.asExternalModel(): SymptomLog =
    SymptomLog(
        id = symptomLogEntity.id,
        date = symptomLogEntity.date,
        items = symptomLogRecords.map { it.asExternalModel() }
    )
