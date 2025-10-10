package com.example.symptomtracker.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.symptomtracker.core.database.model.DrinkItemEntity
import com.example.symptomtracker.core.database.model.DrinkLogEntity
import com.example.symptomtracker.core.database.model.DrinkLogItemCrossRef
import com.example.symptomtracker.core.database.model.PopulatedDrinkLog
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

@Dao
interface DrinkLogDao {
    @Insert
    fun insertDrinkLog(drinkLogEntity: DrinkLogEntity): Long

    @Insert
    suspend fun insertItem(drinkItemEntity: DrinkItemEntity): Long

    @Query("SELECT id FROM drink_item WHERE name = :name")
    fun getItemByName(name: String): Long?

    @Transaction
    suspend fun insertOrGetItemByName(name: String): Long =
        getItemByName(name) ?: insertItem(DrinkItemEntity(id = 0, name = name))

    @Query("SELECT * FROM drink_item ORDER BY name ASC")
    fun getAllItems(): Flow<List<DrinkItemEntity>>

    @Transaction
    @Query("SELECT * FROM drink_log ORDER BY date DESC")
    fun getAllDrinkLogs(): Flow<List<PopulatedDrinkLog>>

    @Transaction
    @Query("SELECT * FROM drink_log WHERE date BETWEEN :startDate AND :endDate")
    fun getAllDrinkLogsBetweenDates(
        startDate: OffsetDateTime,
        endDate: OffsetDateTime
    ): Flow<List<PopulatedDrinkLog>>

    @Transaction
    @Query("SELECT * FROM drink_log WHERE id = :id")
    fun getDrinkLog(id: Long): Flow<PopulatedDrinkLog?>

    @Insert
    suspend fun insertDrinkLogItemCrossRef(drinkLogItemCrossRef: DrinkLogItemCrossRef)

    @Transaction
    suspend fun insertDrinkLogAndAssociatedRecords(populatedDrinkLog: PopulatedDrinkLog) {
        val drinkLogId = insertDrinkLog(populatedDrinkLog.log)

        populatedDrinkLog.drinkItemEntities.forEach { item ->
            insertDrinkLogItemCrossRef(
                drinkLogItemCrossRef = DrinkLogItemCrossRef(
                    drinkLogId = drinkLogId,
                    itemId = item.id
                )
            )
        }
    }

    @Delete
    suspend fun deleteLog(drinkLogEntity: DrinkLogEntity)

    @Query("DELETE FROM drink_log_item WHERE drinkLogId = :id")
    suspend fun deleteAllCrossRefItemsForLogById(id: Long)

    @Query("DELETE FROM drink_log_item WHERE itemId = :id")
    suspend fun deleteAllCrossRefItemsForItemById(id: Long)

    @Update
    suspend fun updateLog(drinkLogEntity: DrinkLogEntity)

    @Update
    suspend fun updateDrinkItem(drinkItemEntity: DrinkItemEntity)

    @Transaction
    suspend fun updateLogAndAssociatedRecords(populatedDrinkLog: PopulatedDrinkLog) {
        deleteAllCrossRefItemsForLogById(populatedDrinkLog.log.id)
        updateLog(populatedDrinkLog.log)

        populatedDrinkLog.drinkItemEntities.forEach { item ->
            insertDrinkLogItemCrossRef(
                drinkLogItemCrossRef = DrinkLogItemCrossRef(
                    drinkLogId = populatedDrinkLog.log.id,
                    itemId = item.id,
                )
            )
        }
    }

    @Transaction
    suspend fun mergeDrinkItems(drinkItem: DrinkItemEntity, drinkItemToMerge: DrinkItemEntity) {
        updateDrinkItemReferences(oldDrinkItemId = drinkItemToMerge.id, newDrinkItemId = drinkItem.id)
        deleteAllCrossRefItemsForItemById(drinkItemToMerge.id)
        deleteDrinkItem(drinkItemToMerge)
    }

    /**
     * Updates all references to the old drink item if the new drink item does not already exist in the same log.
     */
    @Query(
        """
        UPDATE drink_log_item
        SET itemId = :newDrinkItemId
        WHERE itemId = :oldDrinkItemId
        AND NOT EXISTS (
            SELECT 1
            FROM drink_log_item dli
            WHERE drinkLogId = drink_log_item.drinkLogId
            AND itemId = :newDrinkItemId
        )
    """
    )
    suspend fun updateDrinkItemReferences(oldDrinkItemId: Long, newDrinkItemId: Long)

    @Query("SELECT COUNT(*) FROM drink_log_item WHERE itemId = :drinkItemId")
    suspend fun getCountOfLogsItemBelongsTo(drinkItemId: Long): Int

    @Delete
    suspend fun deleteDrinkItem(drinkItemEntity: DrinkItemEntity)
}
