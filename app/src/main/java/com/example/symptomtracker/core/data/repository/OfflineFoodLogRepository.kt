package com.example.symptomtracker.core.data.repository

import com.example.symptomtracker.core.data.model.asEntity
import com.example.symptomtracker.core.data.model.asFoodLogEntity
import com.example.symptomtracker.core.database.dao.FoodLogDao
import com.example.symptomtracker.core.database.model.FoodItemEntity
import com.example.symptomtracker.core.database.model.PopulatedFoodLog
import com.example.symptomtracker.core.database.model.asExternalModel
import com.example.symptomtracker.core.model.FoodItem
import com.example.symptomtracker.core.model.FoodLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime
import javax.inject.Inject

class OfflineFoodLogRepository @Inject constructor(private val foodLogDao: FoodLogDao) :
    FoodLogRepository {
    override suspend fun insertFoodLog(foodLog: FoodLog) =
        foodLogDao.insertFoodLogAndAssociatedRecords(populatedFoodLog = foodLog.asEntity())

    override fun getAllItems(): Flow<List<FoodItem>> =
        foodLogDao.getAllItems().map { it.map(FoodItemEntity::asExternalModel) }

    override fun getAllFoodLogs(): Flow<List<FoodLog>> =
        foodLogDao.getAllFoodLogs().map { it.map(PopulatedFoodLog::asExternalModel) }

    override fun getFoodLog(id: Long): Flow<FoodLog?> =
        foodLogDao.getFoodLog(id).map { it?.asExternalModel() }

    override fun getAllFoodLogsBetweenDates(
        startDate: OffsetDateTime, endDate: OffsetDateTime
    ): Flow<List<FoodLog>> = foodLogDao.getAllFoodLogsBetweenDates(startDate, endDate)
        .map { it.map(PopulatedFoodLog::asExternalModel) }

    override suspend fun insertItem(foodItem: FoodItem): Long =
        foodLogDao.insertItem(foodItemEntity = foodItem.asEntity())

    override suspend fun insertOrGetItemByName(name: String): FoodItem =
        FoodItem(id = foodLogDao.insertOrGetItemByName(name), name = name)

    override suspend fun deleteFoodLog(foodLog: FoodLog) =
        foodLogDao.deleteLog(foodLog.asFoodLogEntity())

    override suspend fun deleteFoodItem(foodItem: FoodItem) =
        foodLogDao.deleteFoodItem(foodItem.asEntity())

    override suspend fun updateFoodLog(foodLog: FoodLog) =
        foodLogDao.updateLogAndAssociatedRecords(populatedFoodLog = foodLog.asEntity())

    override suspend fun updateFoodItem(foodItem: FoodItem) =
        foodLogDao.updateFoodItem(foodItem.asEntity())

    override suspend fun mergeFoodItems(foodItem: FoodItem, foodItemToMerge: FoodItem) =
        foodLogDao.mergeFoodItems(
            foodItem = foodItem.asEntity(),
            foodItemToMerge = foodItemToMerge.asEntity()
        )

    override suspend fun getCountOfLogsItemBelongsTo(foodItem: FoodItem): Int =
        foodLogDao.getCountOfLogsItemBelongsTo(foodItemId = foodItem.id)
}
