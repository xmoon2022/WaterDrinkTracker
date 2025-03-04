package io.github.xmoon2022.water.ui.screen.settings.screens.data_setting.items

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import io.github.xmoon2022.water.ui.screen.settings.SettingItem

@Composable
fun ConfigWebdav(onClick: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
    SettingItem(
        title = "配置 WebDev",
        description = "当前服务端: ${prefs.getString("webdev_url", "未配置")}",
        icon = { Icon(Icons.Default.Settings, "配置") },
        onClick = onClick,
        content = {}
    )
}