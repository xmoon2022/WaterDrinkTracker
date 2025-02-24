package io.github.xmoon2022.water.ui.screen.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavController
import io.github.xmoon2022.water.notification.NotificationHelper
import io.github.xmoon2022.water.ui.screen.settings.items.About
import io.github.xmoon2022.water.ui.screen.settings.items.DailyGoal
import io.github.xmoon2022.water.ui.screen.settings.items.DataSetting
import io.github.xmoon2022.water.ui.screen.settings.items.ShowNotification
import io.github.xmoon2022.water.ui.screen.settings.items.StyleSetting

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
        ShowNotification()
        Spacer(modifier = Modifier.height(4.dp))
        About()
    }
}