package io.github.xmoon2022.water.ui.screen.settings.screens.about_setting.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.xmoon2022.water.ui.screen.settings.SettingsScreen

@Composable
fun DependentScreen(){
    SettingsScreen {
        Text(
            text = "依赖",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

    }
}