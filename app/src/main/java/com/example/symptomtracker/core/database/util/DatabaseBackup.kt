package com.example.symptomtracker.core.database.util

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.example.symptomtracker.MainActivity
import com.example.symptomtracker.core.database.AppDatabase
import java.io.File
import java.io.FileOutputStream
import java.util.Date
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class DatabaseBackup(private val context: Context) {
    fun downloadDatabase() {
        val dbFile = getDatabaseFile()
        val walFile = File(dbFile.absolutePath + "-wal")

        // Values for the file to be saved to the Downloads folder
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "symptom_tracker_backup_${Date().time}.zip")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/zip")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        // Insert the file into the MediaStore
        val uri =
            context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                ?: throw RuntimeException("Failed to create ZIP file in Downloads folder")

        // Close the database before reading it
        AppDatabase.close()

        // Open an output stream and write the database file to it
        context.contentResolver.openOutputStream(uri).use { outputStream ->
            ZipOutputStream(outputStream).use { zipOutputStream ->
                listOf(dbFile, walFile).forEach { file ->
                    if (file.exists()) {
                        zipOutputStream.putNextEntry(ZipEntry(file.name))
                        file.inputStream().use { inputStream ->
                            inputStream.copyTo(zipOutputStream)
                        }
                        zipOutputStream.closeEntry()
                    }
                }
            }
        }

        // Reopen the database
        AppDatabase.getDatabase(context)
    }

    fun restoreDatabase(zipUri: Uri) {
        val inputStream = context.contentResolver.openInputStream(zipUri)
            ?: throw RuntimeException("Failed to open input stream for uri: $zipUri")

        // Close the database before overwriting it
//        AppDatabase.close()

        val dbDir = getDatabaseFile().parentFile

        // Unzip the file
        ZipInputStream(inputStream).use { zipIn ->
            var entry: ZipEntry?
            while (zipIn.nextEntry.also { entry = it } != null) {
                val extractedFile = File(dbDir, entry!!.name)
                FileOutputStream(extractedFile).use { outputStream ->
                    zipIn.copyTo(outputStream)
                }
                zipIn.closeEntry()
            }
        }

//        // Reopen the database
//        AppDatabase.getDatabase(context)

        // Restart the app
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
        if (context is Activity) {
            context.finish()
        }
    }

    private fun getDatabaseFile(): File {
        return context.getDatabasePath(AppDatabase.DATABASE_NAME)
    }
}
