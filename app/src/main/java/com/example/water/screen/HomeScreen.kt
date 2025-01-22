package com.example.water.screen

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
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
fun CheckList(
    modifier: Modifier = Modifier
) {
    // 使用 SharedPreferences 存储复选框状态
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("checklist_prefs", Context.MODE_PRIVATE)
    //Log.d("ResetCheckboxWorker", "Updated checkbox states: ${sharedPreferences.getString("checkbox_states", "")}")
    // 初始化状态
    var checkboxStates by remember(sharedPreferences.getString("checkbox_states", null)) {
        mutableStateOf(
            sharedPreferences.getString("checkbox_states", null)
                ?.split(",")
                ?.map { it.toBoolean() }
                ?: List(8) { false }
        )
    }
    // 监听 SharedPreferences 的变化并更新状态
    // 替换旧的 LaunchedEffect 代码
    DisposableEffect(sharedPreferences) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "checkbox_states") {
                val newStates = sharedPreferences.getString("checkbox_states", null)
                    ?.split(",")
                    ?.map { it.toBoolean() }
                    ?: List(8) { false }
                checkboxStates = newStates
            }
        }
        // 注册监听器
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        // 在组件销毁时自动注销监听器
        onDispose {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
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
            FilledTonalButton(onClick = {
                // 重置所有复选框的状态
                checkboxStates = List(8) { false }
                // 保存状态到 SharedPreferences
                with(sharedPreferences.edit()) {
                    putString("checkbox_states", checkboxStates.joinToString(","))
                    apply()
                }
            })
            {
                Text("重置")
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