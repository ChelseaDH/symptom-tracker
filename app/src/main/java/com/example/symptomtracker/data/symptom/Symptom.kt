package com.example.symptomtracker.data.symptom

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.symptomtracker.ui.symptom.SymptomType
import java.util.*

@Entity(tableName = "symptom")
data class Symptom(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val type: SymptomType,
    val date: Date,
)
