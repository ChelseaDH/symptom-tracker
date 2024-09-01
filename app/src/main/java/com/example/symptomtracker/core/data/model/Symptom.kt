package com.example.symptomtracker.core.data.model

import com.example.symptomtracker.core.database.model.SymptomEntity
import com.example.symptomtracker.core.domain.model.Symptom

fun Symptom.asEntity(): SymptomEntity = SymptomEntity(id = id, name = name)
