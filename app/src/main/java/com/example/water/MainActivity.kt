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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
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

@Composable
fun ResetButton(onClick: () -> Unit) {
    FilledTonalButton(onClick = { onClick() }) {
        Text("重置")
    }
}

@Composable
fun CheckList(
    modifier: Modifier = Modifier
) {
    // 使用 SharedPreferences 存储复选框状态
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("CheckList_prefs", Context.MODE_PRIVATE)
    Log.d("maininit", "Updated checkbox states: ${sharedPreferences.getString("checkbox_states", "")}")
    // 初始化状态
    var checkboxStates by remember {
        mutableStateOf(
            sharedPreferences.getString("checkbox_states", null)
                ?.split(",")
                ?.map { it.toBoolean() }
                ?: List(8) { false }
        )
    }
    // 监听 SharedPreferences 的变化并更新状态
    LaunchedEffect(Unit) {
        snapshotFlow {
            sharedPreferences.getString("checkbox_states", null)
        }.collect { updatedStates ->
            updatedStates?.let {
                checkboxStates = it.split(",").map { state -> state.toBoolean() }
            }
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = modifier) {
            for (i in 0 until 8) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = checkboxStates[i],
                        onCheckedChange = {
                            val updatedStates = checkboxStates.toMutableList()
                            updatedStates[i] = it
                            checkboxStates = updatedStates
                            // 保存状态到 SharedPreferences
                            with(sharedPreferences.edit()) {
                                putString("checkbox_states", updatedStates.joinToString(","))
                                apply()
                            }
                        }
                    )
                    Text(text = "第${i + 1}杯")
                }
            }
            Spacer(Modifier.height(16.dp))
            ResetButton(onClick = {
                // 重置所有复选框的状态
                checkboxStates = List(8) { false }
                // 保存状态到 SharedPreferences
                with(sharedPreferences.edit()) {
                    putString("checkbox_states", checkboxStates.joinToString(","))
                    apply()
                }
            })
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

    val workRequest = PeriodicWorkRequestBuilder<ResetCheckboxWorker>(1, TimeUnit.DAYS)
        .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "resetCheckboxTask",
        ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, // 取消已有任务并重新排队
        workRequest
    )
}

@Composable
fun BottomBar(){
    BottomAppBar(){
        Row (
            modifier = Modifier
                .fillMaxWidth(), // 让Row占据整个宽度
            horizontalArrangement = Arrangement.SpaceEvenly // 图标之间等距分布
        ) {
            IconButton(onClick = { /* do something */ }) {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "首页",
                    modifier = Modifier.size(32.dp)
                )
            }
            IconButton(onClick = { /* do something */ }) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = "日历",
                    modifier = Modifier.size(32.dp)
                )
            }
            IconButton(onClick = { /* do something */ }) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "设置",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun mainscreen(){
    Scaffold(
        bottomBar = {
            BottomBar()
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize() // 让Box占据整个屏幕
                .padding(innerPadding) // 使用Scaffold提供的内边距
        ) {
            CheckList()
        }
    }
}

@Preview(showBackground = true,showSystemUi = true)
@Composable
fun GreetingPreview() {
    waterTheme {
        mainscreen()
    }
}