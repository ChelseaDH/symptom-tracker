package com.example.symptomtracker.data.symptom

import kotlinx.coroutines.flow.Flow

class OfflineSymptomRepository(private val symptomDao: SymptomDao) : SymptomRepository {
    override fun insertSymptom(symptom: Symptom): Long = symptomDao.insertSymptom(symptom = symptom)

    override fun getAllSymptomsStream(): Flow<List<Symptom>> = symptomDao.getAllSymptoms()

    override suspend fun insertSymptomLogWithSymptom(symptomLogWithSymptoms: SymptomLogWithSymptoms) =
        symptomDao.insertSymptomLogWithSymptoms(symptomLogWithSymptoms)
}