package com.example.water.ui.screen.settings.items

import android.content.Context
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.water.R
import com.example.water.ui.screen.settings.SettingItem

@Composable
fun DailyGoal() {
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }

    var dailyGoal by remember {
        mutableIntStateOf(
            sharedPreferences.getInt("daily_goal", 8) // 默认8杯
        )
    }
    SettingItem(
        title = "每日目标",
        description = "设置每日喝水杯数",
        icon = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.assignment),
                contentDescription = "目标图标",
                modifier = Modifier.size(24.dp)
            )
        }
    ) {
        Row(verticalAlignment = CenterVertically) {
            IconButton(
                onClick = {
                    if (dailyGoal > 1) {
                        dailyGoal -= 1
                        sharedPreferences.edit().putInt("daily_goal", dailyGoal).apply()
                    }
                }
            ) {
                Icon(Icons.Default.KeyboardArrowDown, "减少")
            }

            Text(
                "$dailyGoal  杯",
                modifier = Modifier.padding(horizontal = 8.dp),
                style = MaterialTheme.typography.bodyLarge
            )

            IconButton(
                onClick = {
                    if (dailyGoal  < 20) {
                        dailyGoal += 1
                        sharedPreferences.edit().putInt("daily_goal", dailyGoal).apply()
                    }
                }
            ) {
                Icon(Icons.Default.KeyboardArrowUp, "增加")
            }
        }
    }
}