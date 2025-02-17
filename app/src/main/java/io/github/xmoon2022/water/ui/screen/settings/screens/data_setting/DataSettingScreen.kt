package io.github.xmoon2022.water.ui.screen.settings.screens.data_setting

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.xmoon2022.water.navigation.Screen
import io.github.xmoon2022.water.ui.screen.settings.SettingsScreen
import io.github.xmoon2022.water.ui.screen.settings.screens.data_setting.items.LocalBackup
import io.github.xmoon2022.water.ui.screen.settings.screens.data_setting.items.WebdavBackup
import io.github.xmoon2022.water.ui.screen.settings.screens.data_setting.items.WebdavConfig
import io.github.xmoon2022.water.ui.screen.settings.screens.style_setting.items.ChooseStyle
import io.github.xmoon2022.water.ui.screen.settings.screens.style_setting.items.ChooseTheme

@Composable
fun DataSettingScreen(){
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
        WebdavConfig()
    }
}