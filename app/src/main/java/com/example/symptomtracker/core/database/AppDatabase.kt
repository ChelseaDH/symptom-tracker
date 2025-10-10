package com.example.symptomtracker.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.symptomtracker.core.database.dao.DrinkLogDao
import com.example.symptomtracker.core.database.dao.FoodLogDao
import com.example.symptomtracker.core.database.dao.MovementDao
import com.example.symptomtracker.core.database.dao.SymptomDao
import com.example.symptomtracker.core.database.model.DrinkItemEntity
import com.example.symptomtracker.core.database.model.DrinkLogEntity
import com.example.symptomtracker.core.database.model.DrinkLogItemCrossRef
import com.example.symptomtracker.core.database.model.FoodItemEntity
import com.example.symptomtracker.core.database.model.FoodLogEntity
import com.example.symptomtracker.core.database.model.FoodLogItemCrossRef
import com.example.symptomtracker.core.database.model.MovementLogEntity
import com.example.symptomtracker.core.database.model.SymptomEntity
import com.example.symptomtracker.core.database.model.SymptomLogEntity
import com.example.symptomtracker.core.database.model.SymptomLogSymptomCrossRef
import com.example.symptomtracker.core.database.util.Converters

/**
 * Database class with singleton Instance object
 */
@Database(
    entities = [SymptomEntity::class, SymptomLogEntity::class, SymptomLogSymptomCrossRef::class, FoodLogEntity::class, FoodItemEntity::class, FoodLogItemCrossRef::class, DrinkLogEntity::class, DrinkItemEntity::class, DrinkLogItemCrossRef::class, MovementLogEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun symptomDao(): SymptomDao
    abstract fun foodLogDao(): FoodLogDao
    abstract fun drinkLogDao(): DrinkLogDao
    abstract fun movementDao(): MovementDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context = context,
                    klass = AppDatabase::class.java,
                    name = "app_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                    .also { Instance = it }
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE UNIQUE INDEX index_item_name ON item (name)")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create drink_item table
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS drink_item (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE UNIQUE INDEX index_drink_item_name ON drink_item (name)")

                // Create drink_log table
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS drink_log (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        date TEXT NOT NULL
                    )
                    """.trimIndent()
                )

                // Create drink_log_item cross reference table
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS drink_log_item (
                        drinkLogId INTEGER NOT NULL,
                        itemId INTEGER NOT NULL,
                        PRIMARY KEY (drinkLogId, itemId),
                        FOREIGN KEY (drinkLogId) REFERENCES drink_log(id) ON DELETE CASCADE,
                        FOREIGN KEY (itemId) REFERENCES drink_item(id) ON DELETE RESTRICT
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX index_drink_log_item_itemId ON drink_log_item (itemId)")
            }
        }
    }
}
