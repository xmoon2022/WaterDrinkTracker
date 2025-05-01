package io.github.xmoon2022.water.notification

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

class HourlyHydrationWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        HydrationReminderHelper.showHydrationReminder(applicationContext)
        return Result.success()
    }
}

object HydrationReminderScheduler {
    private const val WORKER_TAG = "hourly_hydration_worker"

    fun schedule(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<HourlyHydrationWorker>(
            1, TimeUnit.HOURS,
            15, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORKER_TAG,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context)
            .cancelUniqueWork(WORKER_TAG)
    }
}
