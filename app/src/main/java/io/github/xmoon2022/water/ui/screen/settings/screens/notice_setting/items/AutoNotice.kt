package io.github.xmoon2022.water.ui.screen.settings.screens.notice_setting.items

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import io.github.xmoon2022.water.notification.HydrationReminderScheduler
import io.github.xmoon2022.water.ui.screen.settings.SettingItem


@Composable
fun AutoNotice(){
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val autoNoticeEnabled = rememberSaveable(prefs) {
        mutableStateOf(prefs.getBoolean("auto_notice", false))
    }
    SettingItem(
        title = "定时通知",
        description = "每小时提醒喝水",
        icon = {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = null
            )
        },
        content = {
            Switch(
                checked = autoNoticeEnabled.value,
                onCheckedChange = { enabled ->
                    autoNoticeEnabled.value = enabled
                    handleAutoNoticeChange(context,enabled)
                }
            )
        }
    )
}

fun handleAutoNoticeChange(context: Context, enabled: Boolean) {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    prefs.edit().putBoolean("auto_notice", enabled).apply()
    if (enabled) {
        HydrationReminderScheduler.schedule(context)
    } else {
        HydrationReminderScheduler.cancel(context)
    }
}
