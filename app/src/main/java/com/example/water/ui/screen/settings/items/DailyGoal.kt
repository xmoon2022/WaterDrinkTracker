package com.example.water.ui.screen.settings.items

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
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.water.R
import com.example.water.ui.screen.settings.SettingItem

@Composable
fun DailyGoal(
    currentGoal: Int,
    onGoalChanged: (Int) -> Unit
) {
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
                    if (currentGoal > 1) {
                        onGoalChanged(currentGoal - 1)
                    }
                }
            ) {
                Icon(Icons.Default.KeyboardArrowDown, "减少")
            }

            Text(
                "$currentGoal 杯",
                modifier = Modifier.padding(horizontal = 8.dp),
                style = MaterialTheme.typography.bodyLarge
            )

            IconButton(
                onClick = {
                    if (currentGoal < 20) {
                        onGoalChanged(currentGoal + 1)
                    }
                }
            ) {
                Icon(Icons.Default.KeyboardArrowUp, "增加")
            }
        }
    }
}