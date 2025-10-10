package com.example.symptomtracker.core.testing.repository

import com.example.symptomtracker.core.domain.model.DrinkItem
import com.example.symptomtracker.core.domain.model.DrinkLog
import com.example.symptomtracker.core.domain.repository.DrinkLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime

class TestDrinkRepository : DrinkLogRepository {
    private val itemsFlow: MutableSharedFlow<List<DrinkItem>> = MutableSharedFlow(replay = 1)
    private val drinkLogsFlow: MutableSharedFlow<List<DrinkLog>> = MutableSharedFlow(replay = 1)

    override suspend fun insertItem(drinkItem: DrinkItem): Long = 1

    override suspend fun insertOrGetItemByName(name: String): DrinkItem = DrinkItem(name)

    override suspend fun insertDrinkLog(drinkLog: DrinkLog) {}

    override fun getAllItems(): Flow<List<DrinkItem>> = itemsFlow

    override fun getAllDrinkLogs(): Flow<List<DrinkLog>> = drinkLogsFlow

    override fun getDrinkLog(id: Long): Flow<DrinkLog?> =
        drinkLogsFlow.map { foodLogs -> foodLogs.find { it.id == id } }

    override fun getAllDrinkLogsBetweenDates(
        startDate: OffsetDateTime,
        endDate: OffsetDateTime
    ): Flow<List<DrinkLog>> =
        drinkLogsFlow.map { foodLogs ->
            foodLogs.filter {
                it.date.isAfter(startDate) && it.date.isBefore(
                    endDate
                )
            }
        }

    override suspend fun deleteDrinkLog(drinkLog: DrinkLog) {}

    override suspend fun deleteDrinkItem(drinkItem: DrinkItem) {
        itemsFlow.tryEmit(itemsFlow.replayCache.first().filter { it.id != drinkItem.id })
    }

    override suspend fun updateDrinkLog(drinkLog: DrinkLog) {}

    override suspend fun updateDrinkItem(drinkItem: DrinkItem) {
        itemsFlow.tryEmit(itemsFlow.replayCache.first().map {
            if (it.id == drinkItem.id) drinkItem else it
        })
    }

    override suspend fun mergeDrinkItems(drinkItem: DrinkItem, drinkItemToMerge: DrinkItem) {
        itemsFlow.tryEmit(itemsFlow.replayCache.first().filter { it != drinkItemToMerge })
    }

    override suspend fun getCountOfLogsItemBelongsTo(drinkItem: DrinkItem): Int =
        drinkLogsFlow.replayCache.first().count { it.items.contains(drinkItem) }

    /**
     * A test-only API to allow controlling the list of food items from tests.
     */
    fun sendDrinkItems(topics: List<DrinkItem>) {
        itemsFlow.tryEmit(topics)
    }

    /**
     * A test-only API to allow controlling the list of food logs from tests.
     */
    fun sendDrinkLogs(topics: List<DrinkLog>) {
        drinkLogsFlow.tryEmit(topics)
    }
}
