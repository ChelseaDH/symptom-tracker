package com.example.symptomtracker.core.data.repository

import com.example.symptomtracker.core.data.model.asEntity
import com.example.symptomtracker.core.data.model.asSymptomLogEntity
import com.example.symptomtracker.core.database.dao.SymptomDao
import com.example.symptomtracker.core.database.model.PopulatedSymptomLog
import com.example.symptomtracker.core.database.model.SymptomEntity
import com.example.symptomtracker.core.database.model.asExternalModel
import com.example.symptomtracker.core.domain.repository.SymptomRepository
import com.example.symptomtracker.core.domain.model.Symptom
import com.example.symptomtracker.core.domain.model.SymptomLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime
import javax.inject.Inject

class OfflineSymptomRepository @Inject constructor(private val symptomDao: SymptomDao) :
    SymptomRepository {
    override suspend fun insertSymptom(symptom: Symptom): Long =
        symptomDao.insertSymptom(symptomEntity = symptom.asEntity())

    override suspend fun insertSymptomLog(symptomLog: SymptomLog) =
        symptomDao.insertSymptomLogAndAssociatedEntities(symptomLog.asEntity())

    override fun getAllSymptoms(): Flow<List<Symptom>> =
        symptomDao.getAllSymptoms().map { it.map(SymptomEntity::asExternalModel) }

    override fun getAllSymptomLogs(): Flow<List<SymptomLog>> =
        symptomDao.getAllSymptomLogs().map { it.map(PopulatedSymptomLog::asExternalModel) }

    override fun getAllSymptomLogsBetweenDates(
        startDate: OffsetDateTime, endDate: OffsetDateTime
    ): Flow<List<SymptomLog>> =
        symptomDao.getAllSymptomLogsBetweenDates(startDate, endDate)
            .map { it.map(PopulatedSymptomLog::asExternalModel) }

    override fun getSymptomLogById(id: Long): Flow<SymptomLog?> =
        symptomDao.getSymptomLog(id).map { it?.asExternalModel() }

    override suspend fun deleteSymptomLog(symptomLog: SymptomLog) =
        symptomDao.deleteLog(symptomLog.asSymptomLogEntity())

    override suspend fun updateSymptomLog(symptomLog: SymptomLog) =
        symptomDao.updateLogAndAssociatedRecords(symptomLog.asEntity())
}
