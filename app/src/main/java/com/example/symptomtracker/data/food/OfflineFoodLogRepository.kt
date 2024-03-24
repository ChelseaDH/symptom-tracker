package com.example.symptomtracker.data.food

import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

class OfflineFoodLogRepository(private val foodLogDao: FoodLogDao) : FoodLogRepository {
    override suspend fun insertFoodLogWithItems(foodLogWithItems: FoodLogWithItems) =
        foodLogDao.insertFoodLogWithItems(foodLogWithItems = foodLogWithItems)

    override fun getAllItemsStream() = foodLogDao.getAllItems()

    override fun getAllFoodLogs(): Flow<List<FoodLogWithItems>> = foodLogDao.getAllFoodLogs()
    override fun getAllFoodLogsBetweenDates(
        startDate: OffsetDateTime,
        endDate: OffsetDateTime
    ): Flow<List<FoodLogWithItems>> =
        foodLogDao.getAllFoodLogsBetweenDates(startDate, endDate)

    override suspend fun insertItem(item: Item) = foodLogDao.insertItem(item)
}
