package com.example.symptomtracker.core.data.repository

import com.example.symptomtracker.core.database.dao.SymptomDao
import com.example.symptomtracker.core.database.model.Symptom
import com.example.symptomtracker.core.database.model.SymptomLogWithLinkedRecords
import com.example.symptomtracker.core.database.model.asExternalModel
import com.example.symptomtracker.core.model.SymptomLogWithSymptoms
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime
import javax.inject.Inject

class OfflineSymptomRepository @Inject constructor(private val symptomDao: SymptomDao) :
    SymptomRepository {
    override suspend fun insertSymptom(symptom: Symptom): Long =
        symptomDao.insertSymptom(symptom = symptom)

    override fun getAllSymptomsStream(): Flow<List<Symptom>> = symptomDao.getAllSymptoms()

    override fun getAllSymptomLogs(): Flow<List<SymptomLogWithSymptoms>> =
        symptomDao.getAllSymptomLogs().map { it.map(SymptomLogWithLinkedRecords::asExternalModel) }

    override fun getAllSymptomLogsBetweenDates(
        startDate: OffsetDateTime, endDate: OffsetDateTime
    ): Flow<List<SymptomLogWithSymptoms>> =
        symptomDao.getAllSymptomLogsBetweenDates(startDate, endDate)
            .map { it.map(SymptomLogWithLinkedRecords::asExternalModel) }

    override fun getSymptomLog(id: Long): Flow<SymptomLogWithSymptoms?> =
        symptomDao.getSymptomLog(id).map { it?.asExternalModel() }

    override suspend fun insertSymptomLogWithSymptom(symptomLogWithSymptoms: SymptomLogWithSymptoms) =
        symptomDao.insertSymptomLogWithSymptoms(symptomLogWithSymptoms)

    override suspend fun deleteWithSymptoms(symptomLogWithSymptoms: SymptomLogWithSymptoms) =
        symptomDao.deleteLog(symptomLogWithSymptoms.log)

    override suspend fun updateLog(log: SymptomLogWithSymptoms) =
        symptomDao.updateLogWithSymptoms(log)
}
