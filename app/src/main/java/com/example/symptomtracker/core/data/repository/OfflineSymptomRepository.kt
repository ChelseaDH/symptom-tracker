package com.example.symptomtracker.core.data.repository

import com.example.symptomtracker.core.database.dao.SymptomDao
import com.example.symptomtracker.core.database.model.Symptom
import com.example.symptomtracker.core.database.model.SymptomLogWithLinkedRecords
import com.example.symptomtracker.core.database.model.SymptomLogWithSymptoms
import com.example.symptomtracker.core.database.model.SymptomLogWithSymptomsAndSeverity
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime
import javax.inject.Inject

class OfflineSymptomRepository @Inject constructor(private val symptomDao: SymptomDao) :
    SymptomRepository {
    override suspend fun insertSymptom(symptom: Symptom): Long =
        symptomDao.insertSymptom(symptom = symptom)

    override fun getAllSymptomsStream(): Flow<List<Symptom>> = symptomDao.getAllSymptoms()

    override fun getAllSymptomLogs(): Flow<List<SymptomLogWithSymptoms>> =
        symptomDao.getAllSymptomLogs()

    override fun getAllSymptomLogsBetweenDates(
        startDate: OffsetDateTime, endDate: OffsetDateTime
    ): Flow<List<SymptomLogWithSymptoms>> =
        symptomDao.getAllSymptomLogsBetweenDates(startDate, endDate)

    override fun getSymptomLog(id: Long): Flow<SymptomLogWithSymptoms?> =
        symptomDao.getSymptomLog(id)

    override fun getSymptomLogWithSeverities(id: Long): Flow<SymptomLogWithLinkedRecords?> =
        symptomDao.getSymptomLogWithLinkedRecords(id)

    override suspend fun insertSymptomLogWithSymptom(symptomLogWithSymptoms: SymptomLogWithSymptomsAndSeverity) =
        symptomDao.insertSymptomLogWithSymptoms(symptomLogWithSymptoms)

    override suspend fun deleteWithSymptoms(symptomLogWithSymptoms: SymptomLogWithSymptoms) =
        symptomDao.deleteLog(symptomLogWithSymptoms.log)

    override suspend fun updateLog(log: SymptomLogWithSymptomsAndSeverity) =
        symptomDao.updateLogWithSymptoms(log)
}
