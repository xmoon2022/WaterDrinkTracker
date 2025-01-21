package com.example.water

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
        setContent {
            waterTheme {
                Checklist()
            }
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
fun Checklist(
    modifier: Modifier = Modifier
) {
    // 使用 SharedPreferences 存储复选框状态
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("checklist_prefs", Context.MODE_PRIVATE)
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
        set(Calendar.HOUR_OF_DAY, 14)
        set(Calendar.MINUTE, 17)
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

    Log.d("TimeZone", "now timezone: ${now.timeZone}")
    Log.d("TimeZone", "resetTime timezone: ${resetTime.timeZone}")
    Log.d("now_time", "now: ${now.timeInMillis}")
    Log.d("reset_time", "reset: ${resetTime.timeInMillis}")
    Log.d("initialDelay", "initialDelay: $initialDelay")
//    val workRequest = PeriodicWorkRequestBuilder<ResetCheckboxWorker>(15, TimeUnit.MINUTES) // 设置 15 分钟周期
//        .setInitialDelay(1, TimeUnit.MINUTES) // 1 分钟后执行
//        .build()
    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "resetCheckboxTask",
        ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, // 取消已有任务并重新排队
        workRequest
    )
    val workInfo = WorkManager.getInstance(context)
        .getWorkInfoById(workRequest.id)
        .get()
    if (workInfo != null) {
        Log.d("WorkStatus", "WorkInfo: ${workInfo.state}")
    }
}

@Preview(showBackground = true,showSystemUi = true)
@Composable
fun GreetingPreview() {
    waterTheme {
        Checklist()
    }
}