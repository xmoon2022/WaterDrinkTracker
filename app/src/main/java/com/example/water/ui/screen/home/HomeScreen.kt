package com.example.water.ui.screen.home

import android.content.Context
import android.content.SharedPreferences
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.appwidget.updateAll
import com.example.water.R
import com.example.water.ui.screen.settings.items.DisplayStyle
import com.example.water.widget.GlanceWidget
import com.example.water.utils.DateUtils
import com.example.water.utils.getTodayCount
import com.example.water.utils.saveTodayCount

@Composable
fun CheckList(
    target: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
    var current by remember { mutableIntStateOf(prefs.getTodayCount().coerceIn(0, target)) }

    // 监听SharedPreferences的变化
    val listener = remember {
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "daily_counts") {
                current = prefs.getTodayCount().coerceAtMost(target)
            }
        }
    }

    DisposableEffect(Unit) {
        prefs.registerOnSharedPreferenceChangeListener(listener)
        onDispose {
            prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    // 统一日期检查
    LaunchedEffect(Unit) {
        DateUtils.checkDailyReset(prefs)
        current = prefs.getTodayCount().coerceIn(0, target)
    }

    // 状态同步
    LaunchedEffect(current) {
        prefs.saveTodayCount(current)
        GlanceWidget().updateAll(context)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Center
    ) {
        Column(modifier = modifier) {
            // 根据目标数量生成复选框
            (0 until target).forEach { index ->
                CheckBoxRow(
                    index = index,
                    current = current,
                    target = target,
                    onCheckedChange = { newState ->
                        current = when {
                            newState && index >= current -> index + 1
                            !newState && index < current -> index
                            else -> current
                        }.coerceIn(0, target)
                    }
                )
            }

            Spacer(Modifier.height(16.dp))
            ResetButton(target) { current = 0 }
        }
    }
}

@Composable
private fun CheckBoxRow(
    index: Int,
    current: Int,
    target: Int,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Checkbox(
            checked = index < current,
            onCheckedChange = { checked ->
                // 只允许两种操作：
                // 1. 勾选下一个未勾选的
                // 2. 取消最后一个已勾选的
                if ((checked && index == current) || (!checked && index == current - 1)) {
                    onCheckedChange(checked)
                }
            },
            enabled = index <= current  // 禁用后面的复选框
        )
        Text("第${index + 1}杯")
    }
}

@Composable
private fun ResetButton(target: Int, onClick: () -> Unit) {
    FilledTonalButton(onClick = onClick) {
        Text("重置", fontSize = 16.sp)
    }
}

@Composable
fun InteractiveWaterCard(target: Int) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
    var current by remember { mutableIntStateOf(prefs.getTodayCount().coerceAtMost(target)) }

    // 监听SharedPreferences的变化
    val listener = remember {
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "daily_counts") {
                current = prefs.getTodayCount().coerceAtMost(target)
            }
        }
    }

    DisposableEffect(Unit) {
        prefs.registerOnSharedPreferenceChangeListener(listener)
        onDispose {
            prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    LaunchedEffect(Unit) {
        DateUtils.checkDailyReset(prefs)
        current = prefs.getTodayCount().coerceAtMost(target)
    }

    LaunchedEffect(current) {
        prefs.saveTodayCount(current)
        GlanceWidget().updateAll(context)
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