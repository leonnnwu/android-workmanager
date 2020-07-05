package com.example.background.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.background.OUTPUT_PATH
import timber.log.Timber
import java.io.File
import java.lang.Exception

class CleanupWorker(ctx: Context, parameters: WorkerParameters): Worker(ctx, parameters) {
    override fun doWork(): Result {
        makeStatusNotification("Cleaning up old temporary files", applicationContext)
        sleep()

        return try {
            File(applicationContext.filesDir, OUTPUT_PATH)
                    .takeIf { it.exists() }
                    ?.listFiles()
                    ?.forEach {
                        if (it.name.endsWith(".png")) {
                            val deleted = it.delete()
                            Timber.i("Deleted ${it.name} - $deleted")
                        }
                    }
            Result.success()
        } catch (ex: Exception) {
            Timber.e(ex)
            Result.failure()
        }
    }
}