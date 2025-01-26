package com.example.water.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role.Companion.Switch
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.water.ui.theme.waterTheme

@Composable
fun SettingItem(
    title: String,
    description: String? = null,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            description?.let {
                Text(text = it, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
        content()
    }
}

@Composable
fun DailyGoal(){
    var dailyGoal by remember { mutableIntStateOf(8) }
    SettingItem(title = "每日目标", description = "设置每日喝水杯数") {
        Row(verticalAlignment = CenterVertically) {
            IconButton(onClick = { if (dailyGoal > 1) dailyGoal-- }) {
                Icon(Icons.Default.KeyboardArrowDown, "减少")
            }
            Text("$dailyGoal 杯", modifier = Modifier.padding(horizontal = 8.dp))
            IconButton(onClick = { if (dailyGoal < 20) dailyGoal++ }) {
                Icon(Icons.Default.KeyboardArrowUp, "增加")
            }
        }
    }
}

@Composable
fun CupCapacity(){
    var cupCapacity by remember { mutableIntStateOf(250) }
    var showDialog by remember { mutableStateOf(false) } // 控制对话框显示
    var tempCapacity by remember { mutableIntStateOf(cupCapacity) } // 临时存储滑块值
    SettingItem(title = "单位设置", description = "设置每杯水的量") {
        Text(
            text = "每杯容量：$cupCapacity ml",
            modifier = Modifier
                .clickable { showDialog = true }
        )
        // 对话框
        if (showDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDialog = false
                    tempCapacity = cupCapacity // 重置临时值
                },
                title = { Text("设置杯量") },
                text = {
                    Column {
                        Slider(
                            value = tempCapacity.toFloat(),
                            onValueChange = { tempCapacity = it.toInt() },
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.secondary,
                                activeTrackColor = MaterialTheme.colorScheme.secondary,
                                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                            ),
                            valueRange = 100f..500f, // 设置容量范围（示例值）
                            steps = 8 // 100-500分8步，每步50ml
                        )
                        Text(
                            text = "当前容量：$tempCapacity ml",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            cupCapacity = tempCapacity
                            showDialog = false
                        }
                    ) {
                        Text("确认")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDialog = false
                            tempCapacity = cupCapacity // 重置临时值
                        }
                    ) {
                        Text("取消")
                    }
                }
            )
        }
    }
}

@Composable
fun about(){
    SettingItem(title = "关于") {
        Column {
//                ClickableText(
//                    text = AnnotatedString("隐私政策"),
//                    onClick = { /* 打开网页 */ }
//                )
            Text("版本号：1.0.0", color = Color.Gray)
            Text("开发者：xmoon", color = Color.Gray)
        }
    }
}

@Composable
fun SettingsScreen(){
    Column {
        DailyGoal()
        HorizontalDivider(thickness = 2.dp)
        CupCapacity()
        HorizontalDivider(thickness = 2.dp)
        about()
    }
}

@Composable
@Preview(showBackground = true,showSystemUi = true)
fun SettingPreview(){
    waterTheme {
        SettingsScreen()
    }
}