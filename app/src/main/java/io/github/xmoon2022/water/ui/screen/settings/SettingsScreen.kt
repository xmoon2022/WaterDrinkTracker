package io.github.xmoon2022.water.ui.screen.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.xmoon2022.water.ui.screen.settings.items.CloudBackupItem
import io.github.xmoon2022.water.ui.screen.settings.items.ConfigWebDevItem
import io.github.xmoon2022.water.ui.screen.settings.items.DailyGoal
import io.github.xmoon2022.water.ui.screen.settings.items.DataBackup
import io.github.xmoon2022.water.ui.screen.settings.items.StyleSetting
import io.github.xmoon2022.water.ui.screen.settings.items.about
import io.github.xmoon2022.water.ui.theme.waterTheme

@Composable
fun SettingsScreen(){
    Column {
        DailyGoal()
        HorizontalDivider(thickness = 2.dp)
        StyleSetting()
        HorizontalDivider(thickness = 2.dp)
        DataBackup()
        HorizontalDivider(thickness = 2.dp)
        CloudBackupItem()
        HorizontalDivider(thickness = 2.dp)
        ConfigWebDevItem()
        HorizontalDivider(thickness = 2.dp)
        about()
    }
}

@Composable
@Preview(showBackground = true,showSystemUi = true)
fun SettingPreview(){
    waterTheme {
        SettingsScreen()
    }
}