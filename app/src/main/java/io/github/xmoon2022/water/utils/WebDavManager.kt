package io.github.xmoon2022.water.utils

import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import com.thegrizzlylabs.sardineandroid.Sardine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine
import java.net.URI

object WebDavManager {
    private const val BACKUP_PATH = "water/"  // 确保以斜杠结尾
    private const val BACKUP_PREFIX = "Backup_"

    private fun getSardine(context: Context): Sardine? {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val baseUrl = prefs.getString("webdev_url", "") ?: return null
        val username = prefs.getString("webdev_user", "") ?: return null
        val password = prefs.getString("webdev_pwd", "") ?: return null

        return OkHttpSardine().apply {
            setCredentials(username, password)
        }
    }

    suspend fun uploadBackup(context: Context): Boolean = withContext(Dispatchers.IO) {
        try {
            val sardine = getSardine(context) ?: throw Exception("未配置 WebDAV")
            val baseUrl = getBaseUrl(context) ?: throw Exception("配置不完整")

            val backupDirUrl = "$baseUrl$BACKUP_PATH"
            Log.d("WebDAV", "备份目录: $backupDirUrl")

            // 直接尝试上传文件，如果失败则处理异常
            val tempFile = File.createTempFile("webdav_backup", ".json").apply {
                deleteOnExit()
            }

            val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val data = JSONObject().apply {
                put("daily_goal", prefs.getInt("daily_goal", 8))
                put("daily_counts", prefs.getString("daily_counts", "{}"))
                put("display_style", prefs.getString("display_style", "WATER"))
            }
            tempFile.writeText(data.toString())

            val remoteFileName = "${BACKUP_PREFIX}${System.currentTimeMillis()}.json"
            val newFileUrl = "$backupDirUrl$remoteFileName"

            try {
                sardine.put(newFileUrl, tempFile.readBytes())
                Log.d("WebDAV", "文件上传成功: $newFileUrl")
            } catch (e: Exception) {
                Log.e("WebDAV", "文件上传失败，尝试创建目录", e)
                // 如果上传失败，尝试创建目录
                try {
                    sardine.createDirectory(backupDirUrl)
                    Log.d("WebDAV", "目录创建成功: $backupDirUrl")
                    // 重新尝试上传文件
                    sardine.put(newFileUrl, tempFile.readBytes())
                    Log.d("WebDAV", "文件上传成功: $newFileUrl")
                } catch (e: Exception) {
                    Log.e("WebDAV", "创建目录失败", e)
                    throw e
                }
            }

            // 删除旧备份文件（保留最新）
            try {
                val backupFiles = sardine.list(backupDirUrl)
                    .filter {
                        it.name.startsWith(BACKUP_PREFIX) &&
                                it.name.endsWith(".json")
                    }
                    .sortedByDescending { it.name }

                if (backupFiles.size > 1) {
                    backupFiles.drop(1).forEach { file ->
                        sardine.delete("$backupDirUrl${file.name}")
                    }
                }
            } catch (e: Exception) {
                Log.e("WebDAV", "删除旧备份文件失败", e)
            }

            true
        } catch (e: Exception) {
            Log.e("WebDAV", "上传失败", e)
            false
        }
    }

    // downloadBackup 和 getBaseUrl 保持原有逻辑，路径已修正
    suspend fun downloadBackup(context: Context): Boolean = withContext(Dispatchers.IO) {
        try {
            val sardine = getSardine(context) ?: throw Exception("未配置 WebDAV")
            val baseUrl = getBaseUrl(context) ?: throw Exception("配置不完整")

            // 现在list方法使用正确的目录路径
            val files = sardine.list("$baseUrl$BACKUP_PATH")
                .filter { it.path.endsWith(".json") }
                .sortedByDescending { it.modified }

            if (files.isEmpty()) throw Exception("服务器上没有备份文件")

            val latestFile = files.first()
            // 使用正确的完整路径访问文件
            val inputStream = sardine.get("$baseUrl$BACKUP_PATH${latestFile.name}")

            val json = inputStream.bufferedReader().use { it.readText() }
            val backupData = JSONObject(json)

            context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE).edit().apply {
                putInt("daily_goal", backupData.optInt("daily_goal", 8))
                putString("daily_counts", backupData.optString("daily_counts", "{}"))
                putString("display_style", backupData.optString("display_style", "WATER"))
            }.apply()

            true
        } catch (e: Exception) {
            Log.e("WebDAV", "下载失败", e)
            false
        }
    }

    private fun getBaseUrl(context: Context): String? {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        var baseUrl = prefs.getString("webdev_url", "") ?: return null
        // 确保baseUrl以斜杠结尾
        if (!baseUrl.endsWith("/")) baseUrl += "/"
        return baseUrl
    }
}