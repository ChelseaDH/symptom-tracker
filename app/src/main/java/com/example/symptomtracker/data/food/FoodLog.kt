package com.example.symptomtracker.data.food

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.time.OffsetDateTime

@Entity(tableName = "food_log")
data class FoodLog(
    @PrimaryKey(autoGenerate = true) val foodLogId: Long,
    val date: OffsetDateTime,
)

@Entity(tableName = "item")
data class Item(
    @PrimaryKey(autoGenerate = true) val itemId: Long,
    val name: String,
)

@Entity(
    tableName = "food_log_item",
    primaryKeys = ["foodLogId", "itemId"],
    indices = [Index("itemId")]
)
data class FoodLogItemCrossRef(
    val foodLogId: Long,
    val itemId: Long,
)

data class FoodLogWithItems(
    @Embedded val foodLog: FoodLog,
    @Relation(
        parentColumn = "foodLogId",
        entityColumn = "itemId",
        associateBy = Junction(FoodLogItemCrossRef::class)
    )
    val items: List<Item>,
)