package io.github.xmoon2022.water.ui.screen.settings.screens.data_setting

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.xmoon2022.water.ui.screen.settings.SettingsScreen
import io.github.xmoon2022.water.ui.screen.settings.screens.data_setting.items.ConfigWebdav
import io.github.xmoon2022.water.ui.screen.settings.screens.data_setting.items.HistoryDataChange
import io.github.xmoon2022.water.ui.screen.settings.screens.data_setting.items.LocalBackup
import io.github.xmoon2022.water.ui.screen.settings.screens.data_setting.items.WebdavBackup
import io.github.xmoon2022.water.utils.WebDavConfig

@Composable
fun DataSettingScreen(){
    val showWebdavDialog = remember { mutableStateOf(false) }
    SettingsScreen{
        Text(
            text = "数据",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        LocalBackup()
        Spacer(modifier = Modifier.height(4.dp))
        WebdavBackup()
        Spacer(modifier = Modifier.height(4.dp))
        WebDavConfig(showDialog = showWebdavDialog)
        ConfigWebdav(onClick = { showWebdavDialog.value = true })
        Spacer(modifier = Modifier.height(4.dp))
        HistoryDataChange()
    }
}