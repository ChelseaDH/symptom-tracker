package com.example.symptomtracker.data.symptom

class OfflineSymptomRepository(private val symptomDao: SymptomDao) : SymptomRepository {
    override suspend fun insertSymptom(symptom: Symptom) = symptomDao.insert(symptom = symptom)
}