package com.example.water.ui.widget

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.glance.layout.Spacer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.Button
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.updateAll
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.components.CircleIconButton
import androidx.glance.appwidget.components.FilledButton
import androidx.glance.appwidget.cornerRadius
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.layout.wrapContentSize
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.water.MainActivity
import com.example.water.R
import com.example.water.ui.widget.action.DecrementAction
import com.example.water.ui.widget.action.IncrementAction
import com.example.water.utils.DateUtils
import com.example.water.utils.getTodayCount
import com.example.water.utils.saveTodayCount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class GlanceWidget : GlanceAppWidget() {

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
            GlanceTheme {
                MyContent(count)
            }
        }
    }

    @Composable
    private fun MyContent(count: Int) {
        Column(
            modifier = GlanceModifier.fillMaxSize()
                .background(Color.White),
            verticalAlignment = Alignment.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "今日喝水 $count 杯", modifier = GlanceModifier.padding(12.dp))
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
//                CircleIconButton(
//                    imageProvider = ImageProvider(R.drawable.ic_add),
//                    contentDescription = "增加",
//                    backgroundColor = GlanceTheme.colors.surfaceVariant,
//                    contentColor = GlanceTheme.colors.onSurfaceVariant,
//                    onClick = actionRunCallback<IncrementAction>()
//                )

//                CircleIconButton(
//                    imageProvider = ImageProvider(R.drawable.ic_remove),
//                    contentDescription = "减少",
//                    backgroundColor = GlanceTheme.colors.surfaceVariant,
//                    contentColor = GlanceTheme.colors.onSurfaceVariant,
//                    onClick = actionRunCallback<DecrementAction>()
//                )
                //CircleIconButton在xiaomi上不兼容
                Button(
                    text = "+",
                    onClick = actionRunCallback<IncrementAction>(),
                    modifier = GlanceModifier.background(Color.LightGray)
                )
                Spacer(
                    modifier = GlanceModifier.width(8.dp)
                )
                Button(
                    text = "-",
                    onClick = actionRunCallback<DecrementAction>(),
                    modifier = GlanceModifier.background(Color.LightGray)
                )
            }
        }
    }
}

