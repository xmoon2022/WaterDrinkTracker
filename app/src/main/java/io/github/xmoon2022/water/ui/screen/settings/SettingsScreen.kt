package io.github.xmoon2022.water.ui.screen.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.xmoon2022.water.ui.screen.settings.items.DailyGoal
import io.github.xmoon2022.water.ui.screen.settings.items.DataSetting
import io.github.xmoon2022.water.ui.screen.settings.items.StyleSetting
import io.github.xmoon2022.water.ui.screen.settings.items.about
import io.github.xmoon2022.water.ui.theme.WaterTheme

@Composable
fun SettingsScreen(navController: NavController){
    SettingsScreen{
        Text(
            text = "设置",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        DailyGoal()
        Spacer(modifier = Modifier.height(4.dp))
        StyleSetting(navController)
        Spacer(modifier = Modifier.height(4.dp))
        DataSetting(navController)
        Spacer(modifier = Modifier.height(4.dp))
        about()
    }
}