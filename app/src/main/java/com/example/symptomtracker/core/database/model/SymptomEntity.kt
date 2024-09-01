package com.example.symptomtracker.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.symptomtracker.core.domain.model.Symptom

/**
 * Defines a symptom that a user may log against a symptom log.
 * It has a many to many relationship with [SymptomLogEntity].
 */
@Entity(tableName = "symptom")
data class SymptomEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
)

fun SymptomEntity.asExternalModel(): Symptom = Symptom(id = id, name = name)
