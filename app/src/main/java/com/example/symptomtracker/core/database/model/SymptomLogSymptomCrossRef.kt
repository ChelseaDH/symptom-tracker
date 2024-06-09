package com.example.symptomtracker.core.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.symptomtracker.core.model.Severity

/**
 * Cross reference for many to many relationship between [SymptomLogEntity] and [SymptomEntity].
 */
@Entity(
    tableName = "symptom_log_record",
    indices = [Index(value = ["symptomId"])],
    primaryKeys = ["symptomLogId", "symptomId"],
    foreignKeys = [ForeignKey(
        entity = SymptomLogEntity::class,
        parentColumns = ["id"],
        childColumns = ["symptomLogId"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = SymptomEntity::class,
        parentColumns = ["id"],
        childColumns = ["symptomId"],
        onDelete = ForeignKey.RESTRICT
    )]
)
data class SymptomLogSymptomCrossRef(
    val symptomLogId: Long,
    val symptomId: Long,
    val severity: Severity,
)
