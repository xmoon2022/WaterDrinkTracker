package io.github.xmoon2022.water.ui.screen.settings.screens.notice_setting

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.xmoon2022.water.ui.screen.settings.SettingsScreen
import io.github.xmoon2022.water.ui.screen.settings.screens.notice_setting.items.AutoNotice
import io.github.xmoon2022.water.ui.screen.settings.screens.notice_setting.items.ShowNotification


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NoticeSettingScreen(){
    SettingsScreen{
        Text(
            text = "通知管理",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        ShowNotification()
        Spacer(modifier = Modifier.height(4.dp))
        AutoNotice()
    }
}