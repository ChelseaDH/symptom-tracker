package com.example.symptomtracker.feature.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.CallSuper

class OpenDatabaseDocumentContract: ActivityResultContract<Unit, Uri?>() {
    @CallSuper
    override fun createIntent(context: Context, input: Unit): Intent {
        return Intent(Intent.ACTION_OPEN_DOCUMENT)
            .putExtra(DocumentsContract.EXTRA_INITIAL_URI, MediaStore.Downloads.EXTERNAL_CONTENT_URI)
            .setType("application/zip")
    }

    override fun getSynchronousResult(
        context: Context,
        input: Unit
    ): SynchronousResult<Uri?>? = null

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return intent.takeIf { resultCode == Activity.RESULT_OK }?.data
    }
}
