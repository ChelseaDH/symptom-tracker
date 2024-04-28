package com.example.symptomtracker.core.data.repository

import com.example.symptomtracker.core.database.dao.SymptomDao
import com.example.symptomtracker.core.database.model.Symptom
import com.example.symptomtracker.core.database.model.SymptomLogWithSymptoms
import com.example.symptomtracker.core.database.model.SymptomLogWithSymptomsAndSeverity
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime
import javax.inject.Inject

class OfflineSymptomRepository @Inject constructor(private val symptomDao: SymptomDao) :
    SymptomRepository {
    override fun insertSymptom(symptom: Symptom): Long = symptomDao.insertSymptom(symptom = symptom)

    override fun getAllSymptomsStream(): Flow<List<Symptom>> = symptomDao.getAllSymptoms()

    override fun getAllSymptomLogs(): Flow<List<SymptomLogWithSymptoms>> =
        symptomDao.getAllSymptomLogs()

    override fun getAllSymptomLogsBetweenDates(
        startDate: OffsetDateTime,
        endDate: OffsetDateTime
    ): Flow<List<SymptomLogWithSymptoms>> =
        symptomDao.getAllSymptomLogsBetweenDates(startDate, endDate)

    override suspend fun insertSymptomLogWithSymptom(symptomLogWithSymptoms: SymptomLogWithSymptomsAndSeverity) =
        symptomDao.insertSymptomLogWithSymptoms(symptomLogWithSymptoms)
}
