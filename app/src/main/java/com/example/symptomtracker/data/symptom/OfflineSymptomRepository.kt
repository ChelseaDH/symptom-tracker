package com.example.symptomtracker.data.symptom

import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

class OfflineSymptomRepository(private val symptomDao: SymptomDao) : SymptomRepository {
    override fun insertSymptom(symptom: Symptom): Long = symptomDao.insertSymptom(symptom = symptom)

    override fun getAllSymptomsStream(): Flow<List<Symptom>> = symptomDao.getAllSymptoms()

    override fun getAllSymptomLogs(): Flow<Map<SymptomLog, List<Symptom>>> =
        symptomDao.getAllSymptomLogs()

    override fun getAllSymptomLogsBetweenDates(
        startDate: OffsetDateTime,
        endDate: OffsetDateTime
    ): Flow<List<SymptomLogWithSymptoms>> =
        symptomDao.getAllSymptomLogsBetweenDates(startDate, endDate)

    override suspend fun insertSymptomLogWithSymptom(symptomLogWithSymptoms: SymptomLogWithSymptomsAndSeverity) =
        symptomDao.insertSymptomLogWithSymptoms(symptomLogWithSymptoms)
}
