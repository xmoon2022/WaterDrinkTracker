package com.example.water

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.water.screen.BottomBar
import com.example.water.screen.CalendarScreen
import com.example.water.screen.MainScreen
import com.example.water.screen.SettingsScreen
import com.example.water.ui.theme.waterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            //HideSystemBars()
            val navController = rememberNavController()
            waterTheme {
                Scaffold(
                    bottomBar = { BottomBar(navController = navController) }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "main",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("main") { MainScreen() }
                        composable("calendar") { CalendarScreen() }
                        composable("Settings") { SettingsScreen() }
                    }
                }
            }
        }
    }
}

/*使用后可以关闭系统导航栏*/
//@Composable
//fun HideSystemBars() {
//    val view = LocalView.current
//    val window = (view.context as ComponentActivity).window
//    // 使用 LaunchedEffect 确保系统栏在 UI 加载完成后隐藏
//    LaunchedEffect(Unit) {
//        WindowInsetsControllerCompat(window, view).apply {
//            hide(WindowInsetsCompat.Type.systemBars()) // 隐藏导航栏和状态栏
//            systemBarsBehavior =
//                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE // 设置手势行为
//        }
//    }
//}

@Preview(showBackground = true,showSystemUi = true)
@Composable
fun GreetingPreview() {
    waterTheme {
        MainScreen()
    }
}