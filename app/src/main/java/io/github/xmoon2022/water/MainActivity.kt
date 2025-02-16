package io.github.xmoon2022.water

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import io.github.xmoon2022.water.ui.screen.home.MainScreen
import io.github.xmoon2022.water.ui.theme.waterTheme
import io.github.xmoon2022.water.navigation.MainNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            //HideSystemBars()
            waterTheme {
                MainNavigation()
            }
        }
    }
}

@Preview(showBackground = true,showSystemUi = true)
@Composable
fun GreetingPreview() {
    waterTheme {
        MainScreen()
    }
}