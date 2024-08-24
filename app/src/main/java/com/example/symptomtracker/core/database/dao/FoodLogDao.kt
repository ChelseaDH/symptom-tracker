package com.example.symptomtracker.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.symptomtracker.core.database.model.FoodItemEntity
import com.example.symptomtracker.core.database.model.FoodLogEntity
import com.example.symptomtracker.core.database.model.FoodLogItemCrossRef
import com.example.symptomtracker.core.database.model.PopulatedFoodLog
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

@Dao
interface FoodLogDao {
    @Insert
    fun insertFoodLog(foodLogEntity: FoodLogEntity): Long

    @Insert
    suspend fun insertItem(foodItemEntity: FoodItemEntity): Long

    @Query("SELECT id FROM item WHERE name = :name")
    fun getItemByName(name: String): Long?

    @Transaction
    suspend fun insertOrGetItemByName(name: String): Long =
        getItemByName(name) ?: insertItem(FoodItemEntity(id = 0, name = name))

    @Query("SELECT * FROM item ORDER BY name ASC")
    fun getAllItems(): Flow<List<FoodItemEntity>>

    @Transaction
    @Query("SELECT * FROM food_log ORDER BY date DESC")
    fun getAllFoodLogs(): Flow<List<PopulatedFoodLog>>

    @Transaction
    @Query("SELECT * FROM food_log WHERE date BETWEEN :startDate AND :endDate")
    fun getAllFoodLogsBetweenDates(
        startDate: OffsetDateTime,
        endDate: OffsetDateTime
    ): Flow<List<PopulatedFoodLog>>

    @Transaction
    @Query("SELECT * FROM food_log WHERE id = :id")
    fun getFoodLog(id: Long): Flow<PopulatedFoodLog?>

    @Insert
    suspend fun insertFoodLogItemCrossRef(foodLogItemCrossRef: FoodLogItemCrossRef)

    @Transaction
    suspend fun insertFoodLogAndAssociatedRecords(populatedFoodLog: PopulatedFoodLog) {
        val foodLogId = insertFoodLog(populatedFoodLog.log)

        populatedFoodLog.foodItemEntities.forEach { item ->
            insertFoodLogItemCrossRef(
                foodLogItemCrossRef = FoodLogItemCrossRef(
                    foodLogId = foodLogId,
                    itemId = item.id
                )
            )
        }
    }

    @Delete
    suspend fun deleteLog(foodLogEntity: FoodLogEntity)

    @Query("DELETE FROM food_log_item WHERE foodLogId = :id")
    suspend fun deleteAllCrossRefItemsForLogById(id: Long)

    @Query("DELETE FROM food_log_item WHERE itemId = :id")
    suspend fun deleteAllCrossRefItemsForItemById(id: Long)

    @Update
    suspend fun updateLog(foodLogEntity: FoodLogEntity)

    @Update
    suspend fun updateFoodItem(foodItemEntity: FoodItemEntity)

    @Transaction
    suspend fun updateLogAndAssociatedRecords(populatedFoodLog: PopulatedFoodLog) {
        deleteAllCrossRefItemsForLogById(populatedFoodLog.log.id)
        updateLog(populatedFoodLog.log)

        populatedFoodLog.foodItemEntities.forEach { item ->
            insertFoodLogItemCrossRef(
                foodLogItemCrossRef = FoodLogItemCrossRef(
                    foodLogId = populatedFoodLog.log.id,
                    itemId = item.id,
                )
            )
        }
    }

    @Transaction
    suspend fun mergeFoodItems(foodItem: FoodItemEntity, foodItemToMerge: FoodItemEntity) {
        updateFoodItemReferences(oldFoodItemId = foodItemToMerge.id, newFoodItemId = foodItem.id)
        deleteAllCrossRefItemsForItemById(foodItemToMerge.id)
        deleteFoodItem(foodItemToMerge)
    }

    /**
     * Updates all references to the old food item if the new food item does not already exist in the same log.
     */
    @Query(
        """
        UPDATE food_log_item
        SET itemId = :newFoodItemId
        WHERE itemId = :oldFoodItemId
        AND NOT EXISTS (
            SELECT 1
            FROM food_log_item fli
            WHERE foodLogId = food_log_item.foodLogId
            AND itemId = :newFoodItemId
        )
    """
    )
    suspend fun updateFoodItemReferences(oldFoodItemId: Long, newFoodItemId: Long)

    @Query("SELECT COUNT(*) FROM food_log_item WHERE itemId = :foodItemId")
    suspend fun getCountOfLogsItemBelongsTo(foodItemId: Long): Int

    @Delete
    suspend fun deleteFoodItem(foodItemEntity: FoodItemEntity)
}
