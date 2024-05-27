package com.example.symptomtracker.core.database.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.symptomtracker.core.model.Log
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
    indices = [Index("itemId")],
    primaryKeys = ["foodLogId", "itemId"],
    foreignKeys = [
        ForeignKey(
            entity = FoodLog::class,
            parentColumns = ["foodLogId"],
            childColumns = ["foodLogId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = Item::class,
            parentColumns = ["itemId"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.RESTRICT,
        )
    ]
)
data class FoodLogItemCrossRef(
    val foodLogId: Long,
    val itemId: Long,
)

data class FoodLogWithItems(
    @Embedded val log: FoodLog,
    @Relation(
        parentColumn = "foodLogId",
        entityColumn = "itemId",
        associateBy = Junction(FoodLogItemCrossRef::class)
    )
    val items: List<Item>,
) : Log {
    override fun getDate(): OffsetDateTime = log.date
}
