package com.example.symptomtracker.core.data.repository

import com.example.symptomtracker.core.data.model.asDrinkLogEntity
import com.example.symptomtracker.core.data.model.asEntity
import com.example.symptomtracker.core.database.dao.DrinkLogDao
import com.example.symptomtracker.core.database.model.DrinkItemEntity
import com.example.symptomtracker.core.database.model.PopulatedDrinkLog
import com.example.symptomtracker.core.database.model.asExternalModel
import com.example.symptomtracker.core.domain.model.DrinkItem
import com.example.symptomtracker.core.domain.model.DrinkLog
import com.example.symptomtracker.core.domain.repository.DrinkLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime
import javax.inject.Inject

class OfflineDrinkLogRepository @Inject constructor(private val drinkLogDao: DrinkLogDao) :
    DrinkLogRepository {
    override suspend fun insertDrinkLog(drinkLog: DrinkLog) =
        drinkLogDao.insertDrinkLogAndAssociatedRecords(populatedDrinkLog = drinkLog.asEntity())

    override fun getAllItems(): Flow<List<DrinkItem>> =
        drinkLogDao.getAllItems().map { it.map(DrinkItemEntity::asExternalModel) }

    override fun getAllDrinkLogs(): Flow<List<DrinkLog>> =
        drinkLogDao.getAllDrinkLogs().map { it.map(PopulatedDrinkLog::asExternalModel) }

    override fun getDrinkLog(id: Long): Flow<DrinkLog?> =
        drinkLogDao.getDrinkLog(id).map { it?.asExternalModel() }

    override fun getAllDrinkLogsBetweenDates(
        startDate: OffsetDateTime, endDate: OffsetDateTime
    ): Flow<List<DrinkLog>> = drinkLogDao.getAllDrinkLogsBetweenDates(startDate, endDate)
        .map { it.map(PopulatedDrinkLog::asExternalModel) }

    override suspend fun insertItem(drinkItem: DrinkItem): Long =
        drinkLogDao.insertItem(drinkItemEntity = drinkItem.asEntity())

    override suspend fun insertOrGetItemByName(name: String): DrinkItem =
        DrinkItem(id = drinkLogDao.insertOrGetItemByName(name), name = name)

    override suspend fun deleteDrinkLog(drinkLog: DrinkLog) =
        drinkLogDao.deleteLog(drinkLog.asDrinkLogEntity())

    override suspend fun deleteDrinkItem(drinkItem: DrinkItem) =
        drinkLogDao.deleteDrinkItem(drinkItem.asEntity())

    override suspend fun updateDrinkLog(drinkLog: DrinkLog) =
        drinkLogDao.updateLogAndAssociatedRecords(populatedDrinkLog = drinkLog.asEntity())

    override suspend fun updateDrinkItem(drinkItem: DrinkItem) =
        drinkLogDao.updateDrinkItem(drinkItem.asEntity())

    override suspend fun mergeDrinkItems(drinkItem: DrinkItem, drinkItemToMerge: DrinkItem) =
        drinkLogDao.mergeDrinkItems(
            drinkItem = drinkItem.asEntity(), drinkItemToMerge = drinkItemToMerge.asEntity()
        )

    override suspend fun getCountOfLogsItemBelongsTo(drinkItem: DrinkItem): Int =
        drinkLogDao.getCountOfLogsItemBelongsTo(drinkItemId = drinkItem.id)
}
