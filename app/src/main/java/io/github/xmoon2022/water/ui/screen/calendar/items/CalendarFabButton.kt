package io.github.xmoon2022.water.ui.screen.calendar.items

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import io.github.xmoon2022.water.R
import io.github.xmoon2022.water.utils.WebDavConfig
import io.github.xmoon2022.water.utils.WebDavManager
import kotlinx.coroutines.launch

@Composable
fun CalendarFabButton(snackbarHostState: SnackbarHostState) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isLoading = remember { mutableStateOf(false) }
    val showConfigDialog = remember { mutableStateOf(false) }
    fun isWebdavConfigured(): Boolean {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return !prefs.getString("webdev_url", "").isNullOrEmpty() &&
                !prefs.getString("webdev_user", "").isNullOrEmpty() &&
                !prefs.getString("webdev_pwd", "").isNullOrEmpty()
    }
    Box(modifier = Modifier.padding(16.dp)) {
        FloatingActionButton(
            onClick = {
                scope.launch {
                    if (isWebdavConfigured()) {
                        isLoading.value = true
                        val success = WebDavManager.uploadBackup(context)
                        Toast.makeText(
                            context,
                            if (success) "备份成功" else "备份失败",
                            Toast.LENGTH_SHORT
                        ).show()
                        isLoading.value = false
                    } else {
                        val result = snackbarHostState.showSnackbar(
                            message = "未配置 WebDAV 服务",
                            actionLabel = "立即配置",
                            duration = SnackbarDuration.Short
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            showConfigDialog.value = true
                        }
                    }
                }
            }
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.backup),
                contentDescription = "云备份"
            )
        }
        // 配置对话框
        if (showConfigDialog.value) {
            WebDavConfig(showDialog = showConfigDialog)
        }
    }
}