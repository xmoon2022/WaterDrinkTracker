package com.example.water.screen

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.water.R
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import java.time.LocalDate

@Composable
fun CheckList(
    target: Int, // 新增目标杯数参数
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }

    // 用 target 作为 remember 的 key，确保目标改变时重新初始化
    var checkboxStates by remember(target) {
        mutableStateOf(
            sharedPreferences.getString("checkbox_states", null)
                ?.split(",")
                ?.map { it.toBoolean() }
                ?.let { savedList ->
                    // 处理存储状态与当前目标长度不一致的情况
                    if (savedList.size == target) savedList
                    else List(target) { index -> savedList.getOrElse(index) { false } }
                } ?: List(target) { false }
        )
    }

    // 处理日期变更和自动保存（添加 target 到 LaunchedEffect 依赖）
    LaunchedEffect(Unit) {
        val today = LocalDate.now().toString()
        val lastSavedDate = sharedPreferences.getString("last_saved_date", null)

        if (lastSavedDate != null && lastSavedDate != today) {
            // 读取存储的原始状态（可能包含历史长度）
            val previousStates = sharedPreferences.getString("checkbox_states", null)
                ?.split(",")
                ?.map { it.toBoolean() } ?: List(target) { false }

            // 统计实际选中数量（无论原始长度）
            val previousCount = previousStates.count { it }

            // 更新历史记录
            val historyJson = sharedPreferences.getString("daily_counts", "{}")
            val historyType = object : TypeToken<MutableMap<String, Int>>() {}.type
            val history = Gson().fromJson<MutableMap<String, Int>>(historyJson, historyType) ?: mutableMapOf()
            history[lastSavedDate] = previousCount

            // 根据当前目标重置状态
            val resetStates = List(target) { false }
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

    // 监听状态变化（保持原有逻辑）
    DisposableEffect(sharedPreferences) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "checkbox_states") {
                checkboxStates = sharedPreferences.getString("checkbox_states", null)
                    ?.split(",")
                    ?.map { it.toBoolean() }
                    ?.let { savedList ->
                        if (savedList.size == target) savedList
                        else List(target) { index -> savedList.getOrElse(index) { false } }
                    } ?: List(target) { false }
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        onDispose { sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Center
    ) {
        Column(modifier = modifier) {
            // 根据目标数量动态生成复选框
            for (i in 0 until target) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = checkboxStates[i],
                        onCheckedChange = {
                            val newStates = checkboxStates.toMutableList().apply { this[i] = it }
                            checkboxStates = newStates
                            sharedPreferences.edit()
                                .putString("checkbox_states", newStates.joinToString(","))
                                .apply()

                            val today = LocalDate.now().toString()
                            val count = newStates.count { it }
                            val historyJson = sharedPreferences.getString("daily_counts", "{}")
                            val historyType = object : TypeToken<MutableMap<String, Int>>() {}.type
                            val history = Gson().fromJson<MutableMap<String, Int>>(historyJson, historyType)
                                ?: mutableMapOf()
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
                    val resetStates = List(target) { false }
                    checkboxStates = resetStates
                    sharedPreferences.edit()
                        .putString("checkbox_states", resetStates.joinToString(","))
                        .apply()

                    val today = LocalDate.now().toString()
                    val historyJson = sharedPreferences.getString("daily_counts", "{}")
                    val historyType = object : TypeToken<MutableMap<String, Int>>() {}.type
                    val history = Gson().fromJson<MutableMap<String, Int>>(historyJson, historyType)
                        ?: mutableMapOf()
                    history[today] = 0

                    sharedPreferences.edit()
                        .putString("daily_counts", Gson().toJson(history))
                        .apply()
                }
            ) {
                Text("重置", fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun InteractiveWaterCard(target: Int) {
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }

    // 从 daily_counts 初始化状态
    var current by remember {
        mutableStateOf(
            getTodayCount(sharedPreferences).coerceAtMost(target)
        )
    }

    // 每日自动重置逻辑
    // 修改后的每日重置逻辑
    LaunchedEffect(Unit) {
        // 迁移旧格式数据到 JSON
        val allEntries = sharedPreferences.all
        val history = loadHistory(sharedPreferences).toMutableMap()

        allEntries.forEach { (key, value) ->
            if (key.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) { // 匹配日期格式的键
                history[key] = (value as? Int) ?: 0
                sharedPreferences.edit().remove(key).apply() // 删除旧键
            }
        }

        saveHistory(sharedPreferences, history)
    }

    // 实时保存到 daily_counts
    LaunchedEffect(current) {
        val todayKey = LocalDate.now().toString()
        val history = loadHistory(sharedPreferences).toMutableMap()
        history[todayKey] = current
        saveHistory(sharedPreferences, history)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Center
    ) {
        Card(
            modifier = Modifier
                .width(280.dp)
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "今日饮水",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(24.dp))
                WaterProgress(current, target)
                Spacer(Modifier.height(16.dp))
                ControlButtons(current, target, onValueChange = { newValue ->
                    current = newValue
                    //sharedPreferences.edit().putInt(getTodayKey(), newValue).apply()
                })
            }
        }
    }
}

@Composable
private fun ControlButtons(
    current: Int,
    target: Int,
    onValueChange: (Int) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { onValueChange((current - 1).coerceAtLeast(0)) }) {
            Icon(Icons.Default.KeyboardArrowDown, "Decrease")
        }
        Text(
            "${current}杯",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        IconButton(onClick = {
            if (current < target) {
                onValueChange(current + 1)
            }
        }) {
            Icon(Icons.Default.KeyboardArrowUp, "Increase")
        }
    }
}

// 辅助函数：获取当日记录
private fun getTodayCount(prefs: SharedPreferences): Int {
    val history = loadHistory(prefs)
    return history[LocalDate.now().toString()] ?: 0 // 仅从 JSON 读取
}

// 辅助函数：加载历史记录（增强健壮性）
fun loadHistory(prefs: SharedPreferences): MutableMap<String, Int> {
    return try {
        val json = prefs.getString("daily_counts", "{}") ?: "{}"
        val type = object : TypeToken<MutableMap<String, Int>>() {}.type
        Gson().fromJson(json, type) ?: mutableMapOf()
    } catch (e: Exception) {
        mutableMapOf()
    }
}

// 辅助函数：保存历史记录
private fun saveHistory(prefs: SharedPreferences, history: Map<String, Int>) {
    prefs.edit()
        .putString("daily_counts", Gson().toJson(history))
        .apply()
}

private fun getTodayKey(): String {
    return LocalDate.now().toString()
}

@Composable
fun WaterProgress(current: Int, target: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 水滴容器
        Box(contentAlignment = Center) {
            CircularProgressIndicator(
                progress = { current.toFloat() / target },
                modifier = Modifier.size(200.dp),
                color = Color.Blue.copy(alpha = 0.3f),
                strokeWidth = 8.dp
            )

            Icon(
                painter = painterResource(R.drawable.water),
                contentDescription = null,
                tint = Color.Blue,
                modifier = Modifier.size(80.dp)
            )
        }

        // 数字显示
        Text(
            text = "$current/${target}杯",
            style = MaterialTheme.typography.displaySmall,
            color = Color.Blue
        )
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
    val dailyGoal = sharedPreferences.getInt("daily_goal", 8)
    val displayStyle = remember { mutableStateOf(sharedPreferences.getString("display_style", DisplayStyle.WATER.name) ?: DisplayStyle.WATER.name) }

    DisposableEffect(sharedPreferences) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "display_style") {
                displayStyle.value = sharedPreferences.getString(key, DisplayStyle.WATER.name) ?: DisplayStyle.WATER.name
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        onDispose {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    val displayStyleName = displayStyle.value
    val selectedStyle = try {
        DisplayStyle.valueOf(displayStyleName)
    } catch (e: IllegalArgumentException) {
        sharedPreferences.edit().putString("display_style", DisplayStyle.WATER.name).apply()
        DisplayStyle.WATER
    }

    when (selectedStyle) {
        DisplayStyle.WATER -> InteractiveWaterCard(dailyGoal)
        DisplayStyle.CHECKLIST -> CheckList(dailyGoal)
    }
}


@Composable
@Preview(showBackground = true,showSystemUi = true)
fun home(){
    //CircularWaterTracker(current = 1, target = 8)
    InteractiveWaterCard(8)
}