package com.example.water

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.water.screen.mainscreen
import com.example.water.ui.theme.waterTheme
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        scheduleResetTask(this)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            HideSystemBars()
            waterTheme {
                mainscreen()
            }
        }
    }
}

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

private fun scheduleResetTask(context: Context) {
    val now = Calendar.getInstance()
    val resetTime = Calendar.getInstance().apply {
        timeZone = now.timeZone // 确保时区一致
        set(Calendar.HOUR_OF_DAY, 4)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }

    val initialDelay = if (now.before(resetTime)) {
        resetTime.timeInMillis - now.timeInMillis
    } else {
        resetTime.timeInMillis + TimeUnit.DAYS.toMillis(1) - now.timeInMillis
    }
    //Log.d("ResetCheckboxWorker_initialdelay","initialDelay:${initialDelay}")
    val workRequest = PeriodicWorkRequestBuilder<ResetCheckboxWorker>(1, TimeUnit.DAYS)
        .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "resetCheckboxTask",
        ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, // 取消已有任务并重新排队
        workRequest
    )
}

@Preview(showBackground = true,showSystemUi = true)
@Composable
fun GreetingPreview() {
    waterTheme {
        mainscreen()
    }
}