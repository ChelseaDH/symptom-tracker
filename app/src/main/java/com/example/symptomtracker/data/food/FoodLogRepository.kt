package com.example.symptomtracker.data.food

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert of [FoodLogWithItems] and retrieval of [Item] from a given data source.
 */
interface FoodLogRepository {
    /**
     * Insert [FoodLog] and associated [Item] records via the [FoodLogWithItems] object.
     */
    suspend fun insertFoodLogWithItems(foodLogWithItems: FoodLogWithItems)

    /**
     * Retrieves all [Item] records.
     */
    fun getAllItemsStream(): Flow<List<Item>>

    /**
     * Insert [Item].
     */
    suspend fun insertItem(item: Item)
}