package com.example.water

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class ResetCheckboxWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        // 模拟通过 SharedPreferences 重置状态
        val sharedPreferences = applicationContext.getSharedPreferences("checklist_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("checkbox_states", List(8) { false }.joinToString(","))
            apply()
        }
        return Result.success()
    }
}
