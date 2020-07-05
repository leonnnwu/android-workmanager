package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import com.example.background.R
import timber.log.Timber

class BlurWorker(ctx: Context, params: WorkerParameters): Worker(ctx, params) {

    override fun doWork(): Result {
        val inputUri = inputData.getString(KEY_IMAGE_URI)
        makeStatusNotification("Blurring image", applicationContext)

        return try {
            val picture = BitmapFactory.decodeStream(applicationContext.contentResolver.openInputStream(Uri.parse(inputUri)))
            val output = blurBitmap(picture, applicationContext)

            val outputUri = writeBitmapToFile(applicationContext, output)
            makeStatusNotification("Output is $outputUri", applicationContext)
            Result.success(workDataOf(KEY_IMAGE_URI to outputUri.toString()))
        } catch (throwable: Throwable) {
            Timber.e(throwable, "error applying blur")
            Result.failure()
        }
    }

}