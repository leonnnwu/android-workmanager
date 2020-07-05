package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class SaveImageToFileWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    private val Title by lazy { "Blurred Image" }
    private val dateFormatter by lazy { SimpleDateFormat(
            "yyyy.MM.dd 'at' HH:mm:ss z",
            Locale.getDefault()
    ) }

    override fun doWork(): Result {
        makeStatusNotification("Saving image", applicationContext)
        sleep()

        return try {
            val resourceUri = inputData.getString(KEY_IMAGE_URI)
            val bitmap = BitmapFactory.decodeStream(applicationContext.contentResolver.openInputStream(Uri.parse(resourceUri)))
            val imageUrl = MediaStore.Images.Media.insertImage(applicationContext.contentResolver, bitmap, Title, dateFormatter.format(Date()))
            if (!imageUrl.isNullOrEmpty()) {
                val output = workDataOf(KEY_IMAGE_URI to imageUrl)
                Result.success(output)
            } else {
                Timber.e("Writing to MediaStore failed")
                Result.failure()
            }
        } catch (exception: Exception) {
            Timber.e(exception)
            Result.failure()
        }
    }
}