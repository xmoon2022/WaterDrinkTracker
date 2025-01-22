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
        //Log.d("ResetCheckboxWorker", "Worker is running")
        val sharedPreferences = applicationContext.getSharedPreferences("checklist_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("checkbox_states", List(8) { false }.joinToString(","))
            commit()
        }
        //Log.d("ResetCheckboxWorker", "Updated checkbox states: ${sharedPreferences.getString("checkbox_states", "")}")
        return Result.success()
    }
}
