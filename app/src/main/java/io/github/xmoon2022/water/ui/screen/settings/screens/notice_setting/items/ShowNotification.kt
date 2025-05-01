package io.github.xmoon2022.water.ui.screen.settings.screens.notice_setting.items

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import io.github.xmoon2022.water.notification.NotificationHelper
import io.github.xmoon2022.water.ui.screen.settings.SettingItem

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ShowNotification(){
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val notificationEnabled = remember {
        mutableStateOf(prefs.getBoolean("notification_enabled", false))
    }
    SettingItem(
        title = "状态栏统计",
        description = "在通知栏显示快捷计数器",
        icon = {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = null
            )
        },
        content = {
            Switch(
                checked = notificationEnabled.value,
                onCheckedChange = { enabled ->
                    prefs.edit().putBoolean("notification_enabled", enabled).apply()
                    notificationEnabled.value = enabled

                    if (enabled) {
                        NotificationHelper.showNotification(
                            context = context,
                            prefs = prefs
                        )
                    } else {
                        NotificationManagerCompat.from(context)
                            .cancel(NotificationHelper.NOTIFICATION_ID)
                    }
                }
            )
        }
    )
}