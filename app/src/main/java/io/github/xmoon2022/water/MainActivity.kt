package io.github.xmoon2022.water

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import io.github.xmoon2022.water.navigation.MainNavigation
import io.github.xmoon2022.water.notification.NotificationHelper
import io.github.xmoon2022.water.ui.screen.home.MainScreen
import io.github.xmoon2022.water.ui.theme.WaterTheme

class MainActivity : ComponentActivity() {
    private val prefs by lazy {
        getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationHelper.createNotificationChannel(this)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            //HideSystemBars()
            WaterTheme {
                if (prefs.getBoolean("notification_enabled", true)) {
                    NotificationHelper.showNotification(this, prefs)
                }
                MainNavigation()
            }
        }
    }
}

@Preview(showBackground = true,showSystemUi = true)
@Composable
fun GreetingPreview() {
    WaterTheme {
        MainScreen()
    }
}