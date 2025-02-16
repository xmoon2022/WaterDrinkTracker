package io.github.xmoon2022.water.ui.screen.home.style

import android.content.Context
import android.content.SharedPreferences
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.appwidget.updateAll
import io.github.xmoon2022.water.utils.DateUtils
import io.github.xmoon2022.water.utils.getTodayCount
import io.github.xmoon2022.water.utils.saveTodayCount
import io.github.xmoon2022.water.widget.GlanceWidget

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
            ResetButton { current = 0 }
        }
    }
}

@Composable
private fun CheckBoxRow(
    index: Int,
    current: Int,
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
private fun ResetButton(onClick: () -> Unit) {
    FilledTonalButton(onClick = onClick) {
        Text("重置", fontSize = 16.sp)
    }
}