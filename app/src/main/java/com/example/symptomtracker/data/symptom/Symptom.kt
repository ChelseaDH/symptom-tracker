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
data class SymptomLogCrossRef(
    val symptomId: Long,
    val symptomLogId: Long,
)

data class SymptomLogWithSymptoms(
    @Embedded val symptomLog: SymptomLog,
    @Relation(
        parentColumn = "symptomLogId",
        entityColumn = "symptomId",
        associateBy = Junction(SymptomLogCrossRef::class)
    )
    val symptoms: List<Symptom>,
)