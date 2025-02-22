package io.github.xmoon2022.water.widget

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.glance.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.GlanceTheme.colors
import androidx.glance.Image
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.components.CircleIconButton
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.wear.tiles.border
import io.github.xmoon2022.water.R
import io.github.xmoon2022.water.widget.action.DecrementAction
import io.github.xmoon2022.water.widget.action.IncrementAction
import io.github.xmoon2022.water.utils.DateUtils
import io.github.xmoon2022.water.utils.getTodayCount

// 宽布局微件 (2x1)
class WideWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            var count by remember { mutableIntStateOf(prefs.getTodayCount())}
            DateUtils.checkDailyReset(prefs) // 触发日期变更逻辑
            // 监听SharedPreferences的变化
            val listener = remember {
                SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                    if (key == "daily_counts") {
                        count = prefs.getTodayCount()
                    }
                }
            }

            DisposableEffect(Unit) {
                prefs.registerOnSharedPreferenceChangeListener(listener)
                onDispose {
                    prefs.unregisterOnSharedPreferenceChangeListener(listener)
                }
            }
            GlanceTheme{
                WideLayoutContent(count)
            }
        }
    }
    @Composable
    private fun WideLayoutContent(count: Int) {
        Column(
            modifier = GlanceModifier.fillMaxSize()
                .background(Color.White),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "今日喝水 $count 杯", modifier = GlanceModifier.padding(12.dp))
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircleIconButton(
                    imageProvider = ImageProvider(R.drawable.ic_add),
                    contentDescription = "增加",
                    backgroundColor = colors.surfaceVariant,
                    contentColor = colors.onSurfaceVariant,
                    onClick = actionRunCallback<IncrementAction>()
                )
                Spacer(modifier = GlanceModifier.width(8.dp))
                CircleIconButton(
                    imageProvider = ImageProvider(R.drawable.ic_remove),
                    contentDescription = "减少",
                    backgroundColor = colors.surfaceVariant,
                    contentColor = colors.onSurfaceVariant,
                    onClick = actionRunCallback<DecrementAction>()
                )
            }
        }
    }
}

// 窄布局微件 (1x2)
class NarrowWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            var count by remember { mutableIntStateOf(prefs.getTodayCount())}
            DateUtils.checkDailyReset(prefs) // 触发日期变更逻辑
            // 监听SharedPreferences的变化
            val listener = remember {
                SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                    if (key == "daily_counts") {
                        count = prefs.getTodayCount()
                    }
                }
            }

            DisposableEffect(Unit) {
                prefs.registerOnSharedPreferenceChangeListener(listener)
                onDispose {
                    prefs.unregisterOnSharedPreferenceChangeListener(listener)
                }
            }
            GlanceTheme{
                NarrowLayoutContent(count)
            }
        }
    }

    @Composable
    private fun NarrowLayoutContent(count: Int) {
        Column(
            modifier = GlanceModifier.fillMaxSize()
                .background(Color.White),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 上方增加按钮
            CircleIconButton(
                imageProvider = ImageProvider(R.drawable.ic_add),
                contentDescription = "增加",
                backgroundColor = colors.surfaceVariant,
                contentColor = colors.onSurfaceVariant,
                onClick = actionRunCallback<IncrementAction>(),
                modifier = GlanceModifier.size(24.dp)
            )

            // 圆形计数显示
            Text(
                text = count.toString(),
                style = TextStyle(
                    color = colors.onSurfaceVariant, // 与按钮颜色一致
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            // 下方减少按钮
            CircleIconButton(
                imageProvider = ImageProvider(R.drawable.ic_remove),
                contentDescription = "减少",
                backgroundColor = colors.surfaceVariant,
                contentColor = colors.onSurfaceVariant,
                onClick = actionRunCallback<DecrementAction>(),
                modifier = GlanceModifier.size(24.dp)
            )
        }
    }
}