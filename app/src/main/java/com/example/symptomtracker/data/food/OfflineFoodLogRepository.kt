package com.example.symptomtracker.data.food

class OfflineFoodLogRepository(private val foodLogDao: FoodLogDao) : FoodLogRepository {
    override suspend fun insertFoodLogWithItems(foodLogWithItems: FoodLogWithItems) =
        foodLogDao.insertFoodLogWithItems(foodLogWithItems = foodLogWithItems)

    override fun getAllItemsStream() = foodLogDao.getAllItems()

    override suspend fun insertItem(item: Item) = foodLogDao.insertItem(item)
}