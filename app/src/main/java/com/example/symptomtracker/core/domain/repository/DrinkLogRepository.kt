package com.example.symptomtracker.core.domain.repository

import com.example.symptomtracker.core.domain.model.DrinkItem
import com.example.symptomtracker.core.domain.model.DrinkLog
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

/**
 * Data layer implementation for [DrinkItem] and [DrinkLog].
 */
interface DrinkLogRepository {
    /**
     * Inserts an item.
     */
    suspend fun insertItem(drinkItem: DrinkItem): Long

    /**
     * Inserts an item with a given name if one does not exist, returning the ID of the existing record if it does.
     */
    suspend fun insertOrGetItemByName(name: String): DrinkItem

    /**
     * Inserts a drink log and it's associated items.
     */
    suspend fun insertDrinkLog(drinkLog: DrinkLog)

    /**
     * Get all drink item records.
     */
    fun getAllItems(): Flow<List<DrinkItem>>

    /**
     * Get all drink logs with their associated items.
     */
    fun getAllDrinkLogs(): Flow<List<DrinkLog>>

    /**
     * Get a drink log with it's associated items, if it exists.
     */
    fun getDrinkLog(id: Long): Flow<DrinkLog?>

    /**
     * Get all drink logs with their associated items between two dates.
     */
    fun getAllDrinkLogsBetweenDates(
        startDate: OffsetDateTime, endDate: OffsetDateTime
    ): Flow<List<DrinkLog>>

    /**
     * Deletes a drink log and the links to it's associated items.
     */
    suspend fun deleteDrinkLog(drinkLog: DrinkLog)

    /**
     * Deletes a drink item.
     */
    suspend fun deleteDrinkItem(drinkItem: DrinkItem)

    /**
     * Updates a drink log and it's associated items.
     */
    suspend fun updateDrinkLog(drinkLog: DrinkLog)

    /**
     * Updates a drink item.
     */
    suspend fun updateDrinkItem(drinkItem: DrinkItem)

    /**
     * Merges two drink items.
     */
    suspend fun mergeDrinkItems(drinkItem: DrinkItem, drinkItemToMerge: DrinkItem)

    /**
     * Gets a count of the drink logs that an item belongs to.
     */
    suspend fun getCountOfLogsItemBelongsTo(drinkItem: DrinkItem): Int
}
