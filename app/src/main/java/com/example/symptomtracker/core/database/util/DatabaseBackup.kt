package com.example.symptomtracker.core.database.util

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import com.example.symptomtracker.core.database.AppDatabase
import java.io.File
import java.util.Date

class DatabaseBackup(private val context: Context) {
    fun downloadDatabase() {
        val dbFile = File(context.getDatabasePath(AppDatabase.DATABASE_NAME).path)

        // Values for the file to be saved to the Downloads folder
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "symptom_tracker_backup_${Date().time}.db")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/octet-stream")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        // Insert the file into the MediaStore
        val uri =
            context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                ?: throw RuntimeException("Failed to create file in Downloads folder")

        // Open an output stream and write the database file to it
        return context.contentResolver.openOutputStream(uri).use { outputStream ->
            dbFile.inputStream().use { inputStream ->
                inputStream.copyTo(outputStream!!)
            }
        }
    }
}
