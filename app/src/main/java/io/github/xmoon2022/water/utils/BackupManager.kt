package io.github.xmoon2022.water.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import androidx.preference.PreferenceManager
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import io.github.xmoon2022.water.utils.BackupManager.createBackup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

object BackupManager {
    private const val PREFS_NAME = "app_prefs"
    private const val BACKUP_PREFIX = "Backup_"

    fun hasBackupLocation(context: Context): Boolean {
        return getBackupDir(context) != null
    }

    fun createBackup(context: Context, callback: (Boolean) -> Unit) {
        try {
            val dir = getBackupDir(context) ?: run {
                Toast.makeText(context, "请先选择备份目录", Toast.LENGTH_SHORT).show()
                callback(false)
                return
            }

            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val data = JSONObject().apply {
                put("daily_goal", prefs.getInt("daily_goal", 8))
                put("daily_counts", prefs.getString("daily_counts", "{}"))
                put("display_style", prefs.getString("display_style", "WATER"))
            }

            // 创建带时间戳的文件
            val filename = "${BACKUP_PREFIX}${System.currentTimeMillis()}.json"
            val file = dir.createFile("application/json", filename)
                ?: throw IOException("无法创建文件")

            context.contentResolver.openOutputStream(file.uri)?.use {
                it.write(data.toString().toByteArray())
                cleanupOldBackups(dir)
                callback(true)
            } ?: callback(false)

        } catch (e: Exception) {
            Log.e("Backup", "备份失败", e)
            callback(false)
        }
    }

    private fun getBackupDir(context: Context): DocumentFile? {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString("backup_dir_uri", null)
            ?.let { Uri.parse(it) }
            ?.let { DocumentFile.fromTreeUri(context, it) }
    }

    private fun cleanupOldBackups(dir: DocumentFile) {
        dir.listFiles()
            .filter { it.name?.startsWith(BACKUP_PREFIX) == true }
            .sortedByDescending { it.lastModified() }
            .drop(1)
            .forEach { it.delete() }
    }


    // 智能恢复逻辑
    fun restoreBackup(context: Context, uri: Uri, callback: (Boolean) -> Unit) {
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val json = inputStream.bufferedReader().use { it.readText() }
                val backupData = JSONObject(json)

                val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                prefs.edit().apply {
                    putInt("daily_goal", backupData.optInt("daily_goal", 8))
                    putString("daily_counts", backupData.optString("daily_counts", "{}"))
                    putString("display_style", backupData.optString("display_style", "WATER"))
                }.apply()

                // 修正参数类型问题
                DateUtils.checkDailyReset(prefs) // 传入SharedPreferences实例
                callback(true)
            }
        } catch (e: JSONException) {
            Toast.makeText(context, "无效的备份文件格式\n请检查文件格式与版本", Toast.LENGTH_LONG).show()
            callback(false)
        } catch (e: Exception) {
            Log.e("BackupManager", "恢复失败", e)
            callback(false)
        }
    }

    // 自动备份管理
    fun setupAutoBackup(context: Context, enable: Boolean) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork("auto_backup")

        if (enable) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresBatteryNotLow(true)
                .build()

            val backupRequest = PeriodicWorkRequestBuilder<AutoBackupWorker>(1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .setInitialDelay(1, TimeUnit.HOURS) // 避免立即执行
                .build()

            workManager.enqueueUniquePeriodicWork(
                "auto_backup",
                ExistingPeriodicWorkPolicy.REPLACE,
                backupRequest
            )
        }
    }
}

// 自动备份Worker
class AutoBackupWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            var success = false
            createBackup(applicationContext) { result ->
                success = result
            }
            if (success) Result.success() else Result.retry()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}