package io.github.xmoon2022.water.ui.screen.settings.screens.about_setting.items

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import io.github.xmoon2022.water.R
import io.github.xmoon2022.water.ui.screen.settings.SettingItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun CheckUpgrade() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val showDialog = remember { mutableStateOf(false) }
    val latestVersion = remember { mutableStateOf("") }
    val checkError = remember { mutableStateOf<String?>(null) }
    val currentVersion = stringResource(id = R.string.version)
    // 显示错误提示
    checkError.value?.let { error ->
        LaunchedEffect(error) {
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            checkError.value = null
        }
    }

    SettingItem(
        title = "检查应用更新",
        icon = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.cach),
                contentDescription = "检测更新图标",
                modifier = Modifier.size(24.dp)
            )
        },
        onClick = {
            scope.launch {
                try {
                    val latest = getLatestGitHubVersion()
                    if (compareVersions(currentVersion, latest)) {
                        latestVersion.value = latest
                        showDialog.value = true
                    } else {
                        Toast.makeText(context, "当前已是最新版本", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    checkError.value = "检查更新失败: ${e.localizedMessage}"
                }
            }
        },
        content = {}
    )

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("发现新版本") },
            text = { Text("最新版本: ${latestVersion.value}\n是否前往下载？") },
            confirmButton = {
                TextButton({
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://github.com/xmoon2022/WaterDrinkTracker/releases/latest")
                    }
                    context.startActivity(intent)
                    showDialog.value = false
                }) { Text("立即更新") }
            },
            dismissButton = {
                TextButton({ showDialog.value = false }) { Text("取消") }
            }
        )
    }
}

// 比较版本号
private fun compareVersions(current: String, latest: String): Boolean {
    fun String.cleanVersion() = replace("[^\\d.]".toRegex(), "").split('.')

    val currentParts = current.cleanVersion()
    val latestParts = latest.cleanVersion()

    for (i in 0 until maxOf(currentParts.size, latestParts.size)) {
        val cur = currentParts.getOrElse(i) { "0" }.toIntOrNull() ?: 0
        val lat = latestParts.getOrElse(i) { "0" }.toIntOrNull() ?: 0
        if (lat > cur) return true
        if (lat < cur) return false
    }
    return false
}

// 获取GitHub最新版本
private suspend fun getLatestGitHubVersion(): String = withContext(Dispatchers.IO) {
    val url = URL("https://api.github.com/repos/xmoon2022/WaterDrinkTracker/releases/latest")
    (url.openConnection() as HttpURLConnection).run {
        requestMethod = "GET"
        connect()

        if (responseCode == 200) {
            inputStream.bufferedReader().use {
                val json = JSONObject(it.readText())
                return@withContext json.getString("tag_name").removePrefix("v")
            }
        } else {
            throw IOException("HTTP error: $responseCode")
        }
    }
}