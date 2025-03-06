package io.github.xmoon2022.water.ui.screen.settings.screens.about_setting

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.xmoon2022.water.ui.screen.settings.SettingsScreen
import io.github.xmoon2022.water.ui.screen.settings.SettingsSection
import io.github.xmoon2022.water.ui.screen.settings.screens.about_setting.items.CheckUpgrade
import io.github.xmoon2022.water.ui.screen.settings.screens.about_setting.items.Dependent
import io.github.xmoon2022.water.ui.screen.settings.screens.about_setting.items.SourceCode

@Composable
fun AboutSettingScreen(navController: NavController){
    SettingsScreen {
        Text(
            text = "关于",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        CheckUpgrade()
        Spacer(modifier = Modifier.height(4.dp))
        SettingsSection(
            title = "联系方式"
        ){
            SourceCode()
        }
        SettingsSection(
            title = "关于本应用"
        ){
            Dependent(navController)
        }
    }
}