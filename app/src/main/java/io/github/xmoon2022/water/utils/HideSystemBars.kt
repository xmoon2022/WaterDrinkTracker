package io.github.xmoon2022.water.utils

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/*使用后可以关闭系统导航栏*/
@Composable
fun HideSystemBars() {
    val view = LocalView.current
    val window = (view.context as ComponentActivity).window
    // 使用 LaunchedEffect 确保系统栏在 UI 加载完成后隐藏
    LaunchedEffect(Unit) {
        WindowInsetsControllerCompat(window, view).apply {
            hide(WindowInsetsCompat.Type.systemBars()) // 隐藏导航栏和状态栏
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE // 设置手势行为
        }
    }
}