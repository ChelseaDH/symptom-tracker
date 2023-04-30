package com.example.symptomtracker.data.symptom

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface SymptomDao {
    @Insert
    suspend fun insert(symptom: Symptom)
}