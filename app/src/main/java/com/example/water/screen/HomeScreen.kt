package com.example.water.screen

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CheckList(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("checklist_prefs", Context.MODE_PRIVATE) }
    var checkboxStates by remember {
        mutableStateOf(
            sharedPreferences.getString("checkbox_states", null)
                ?.split(",")
                ?.map { it.toBoolean() }
                ?: List(8) { false }
        )
    }

    // 处理日期变更和自动保存
    LaunchedEffect(Unit) {
        val today = LocalDate.now().toString()
        val lastSavedDate = sharedPreferences.getString("last_saved_date", null)

        if (lastSavedDate != null && lastSavedDate != today) {
            // 保存前一天的计数
            val previousStates = sharedPreferences.getString("checkbox_states", null)
                ?.split(",")
                ?.map { it.toBoolean() } ?: List(8) { false }
            val previousCount = previousStates.count { it }

            // 更新历史记录
            val historyJson = sharedPreferences.getString("daily_counts", "{}")
            val historyType = object : TypeToken<MutableMap<String, Int>>() {}.type
            val history = Gson().fromJson<MutableMap<String, Int>>(historyJson, historyType) ?: mutableMapOf()
            history[lastSavedDate] = previousCount

            // 重置当天状态
            val resetStates = List(8) { false }
            with(sharedPreferences.edit()) {
                putString("daily_counts", Gson().toJson(history))
                putString("checkbox_states", resetStates.joinToString(","))
                putString("last_saved_date", today)
                apply()
            }
            checkboxStates = resetStates
        } else if (lastSavedDate == null) {
            sharedPreferences.edit().putString("last_saved_date", today).apply()
        }
    }

    // 监听状态变化
    DisposableEffect(sharedPreferences) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "checkbox_states") {
                checkboxStates = sharedPreferences.getString("checkbox_states", null)
                    ?.split(",")
                    ?.map { it.toBoolean() } ?: List(8) { false }
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        onDispose { sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener) }
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
                            val newStates = checkboxStates.toMutableList().apply { this[i] = it }
                            checkboxStates = newStates

                            // 更新复选框状态
                            sharedPreferences.edit()
                                .putString("checkbox_states", newStates.joinToString(","))
                                .apply()

                            // 更新当日计数
                            val today = LocalDate.now().toString()
                            val count = newStates.count { it }
                            val historyJson = sharedPreferences.getString("daily_counts", "{}")
                            val historyType = object : TypeToken<MutableMap<String, Int>>() {}.type
                            val history = Gson().fromJson<MutableMap<String, Int>>(historyJson, historyType) ?: mutableMapOf()
                            history[today] = count

                            sharedPreferences.edit()
                                .putString("daily_counts", Gson().toJson(history))
                                .apply()
                        }
                    )
                    Text("第${i + 1}杯")
                }
            }

            Spacer(Modifier.height(16.dp))
            FilledTonalButton(
                onClick = {
                    // 重置当日状态
                    val resetStates = List(8) { false }
                    checkboxStates = resetStates
                    sharedPreferences.edit()
                        .putString("checkbox_states", resetStates.joinToString(","))
                        .apply()

                    // 更新当日计数为0
                    val today = LocalDate.now().toString()
                    val historyJson = sharedPreferences.getString("daily_counts", "{}")
                    val historyType = object : TypeToken<MutableMap<String, Int>>() {}.type
                    val history = Gson().fromJson<MutableMap<String, Int>>(historyJson, historyType) ?: mutableMapOf()
                    history[today] = 0

                    sharedPreferences.edit()
                        .putString("daily_counts", Gson().toJson(history))
                        .apply()
                }
            ) {
                Text("重置")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Mainscreen(){
//    Scaffold(
//        bottomBar = {
//            BottomBar()
//        }
//    ) { innerPadding ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize() // 让Box占据整个屏幕
//                .padding(innerPadding) // 使用Scaffold提供的内边距
//        ) {
            CheckList()
        //}
    //}
}