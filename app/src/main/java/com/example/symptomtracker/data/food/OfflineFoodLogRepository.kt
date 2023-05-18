package com.example.symptomtracker.data.food

import kotlinx.coroutines.flow.Flow

class OfflineFoodLogRepository(private val foodLogDao: FoodLogDao) : FoodLogRepository {
    override suspend fun insertFoodLogWithItems(foodLogWithItems: FoodLogWithItems) =
        foodLogDao.insertFoodLogWithItems(foodLogWithItems = foodLogWithItems)

    override fun getAllItemsStream() = foodLogDao.getAllItems()

    override fun getAllFoodLogs(): Flow<Map<FoodLog, List<Item>>> = foodLogDao.getAllFoodLogs()

    override suspend fun insertItem(item: Item) = foodLogDao.insertItem(item)
}