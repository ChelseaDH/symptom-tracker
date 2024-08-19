package com.example.symptomtracker.core.database.di

import android.content.Context
import androidx.room.Room
import com.example.symptomtracker.core.database.AppDatabase
import com.example.symptomtracker.core.database.util.DatabaseBackup
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Provides
    @Singleton
    fun providesAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    @Provides
    @Singleton
    fun providesDatabaseBackup(@ApplicationContext context: Context): DatabaseBackup {
        return DatabaseBackup(context)
    }
}
