package com.example.symptomtracker.data.food

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodLogDao {
    @Insert
    fun insertFoodLog(foodLog: FoodLog): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItems(items: List<Item>): Array<Long>

    @Insert
    suspend fun insertItem(item: Item)

    @Query("SELECT * FROM item ORDER BY name ASC")
    fun getAllItems(): Flow<List<Item>>

    @Transaction
    @Query(
        "SELECT * FROM food_log fl " +
                "JOIN food_log_item fli ON fli.food_log_id = fl.id " +
                "JOIN item i ON fli.item_id = i.id "
    )
    fun getAllFoodLogs(): Flow<Map<FoodLog, List<Item>>>

    @Insert
    suspend fun insertFoodLogItemCrossRef(foodLogItemCrossRef: FoodLogItemCrossRef)

    @Transaction
    suspend fun insertFoodLogWithItems(foodLogWithItems: FoodLogWithItems) {
        val foodLogId = insertFoodLog(foodLogWithItems.foodLog)
        val itemLogIds = insertItems(foodLogWithItems.items)

        itemLogIds.forEach { itemId ->
            insertFoodLogItemCrossRef(foodLogItemCrossRef = FoodLogItemCrossRef(
                foodLogId = foodLogId,
                itemId = itemId
            ))
        }
    }
}