package io.github.xmoon2022.water.ui.screen.settings.screens.style_setting

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.xmoon2022.water.ui.screen.settings.SettingsScreen
import io.github.xmoon2022.water.ui.screen.settings.screens.style_setting.items.ChooseStyle
import io.github.xmoon2022.water.ui.screen.settings.screens.style_setting.items.ChooseTheme

@Composable
fun StyleSettingScreen(){
    SettingsScreen{
        Text(
            text = "外观",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        ChooseStyle()
        Spacer(modifier = Modifier.height(4.dp))
        ChooseTheme()
    }
}