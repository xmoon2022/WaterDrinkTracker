package com.example.water.ui.screen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.DocumentsContract
import android.provider.Settings.System.putString
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.example.water.ui.theme.waterTheme
import com.example.water.utils.BackupManager
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingItem(
    title: String,
    description: String? = null,
    onClick: () -> Unit = {}, // 新增点击回调
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() } // 整个区域可点击
            .padding(16.dp),
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
        content()
    }
}

@Composable
fun DailyGoal(
    currentGoal: Int,
    onGoalChanged: (Int) -> Unit
) {
    SettingItem(title = "每日目标", description = "设置每日喝水杯数") {
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

enum class DisplayStyle {
    WATER, CHECKLIST
}

private fun getStyleName(style: String): String {
    return when(DisplayStyle.valueOf(style)) {
        DisplayStyle.WATER -> "水球"
        DisplayStyle.CHECKLIST -> "清单"
    }
}

@Composable
fun StyleSetting() {
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
    val showDialog = remember { mutableStateOf(false) }
    val currentStyle = remember {
        sharedPreferences.getString("display_style", DisplayStyle.WATER.name) ?: DisplayStyle.WATER.name
    }

    SettingItem(
        title = "显示样式",
        description = "当前样式：${getStyleName(currentStyle)}",
        onClick = { showDialog.value = true }
    ) {
        Icon(Icons.Default.ArrowDropDown, "展开")
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("选择显示样式") },
            text = {
                Column {
                    DisplayStyle.entries.forEach { style ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    sharedPreferences.edit()
                                        .putString("display_style", style.name)
                                        .apply()
                                    showDialog.value = false
                                }
                                .padding(12.dp),
                            verticalAlignment = CenterVertically
                        ) {
                            RadioButton(
                                selected = style.name == currentStyle,
                                onClick = null
                            )
                            Text(
                                text = when (style) {
                                    DisplayStyle.WATER -> "水球样式"
                                    DisplayStyle.CHECKLIST -> "清单样式"
                                },
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog.value = false }) {
                    Text("关闭")
                }
            }
        )
    }
}

private fun getTimestamp(): String {
    return SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
}

@Composable
fun DataBackup(){
    val context = LocalContext.current
    val showList = remember { mutableStateOf(false) }
    var autoBack = remember { mutableStateOf(false) }
    // 备份相关状态控制
    val backupDirLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let {
            // 保存目录并启用自动备份
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            PreferenceManager.getDefaultSharedPreferences(context).edit {
                putString("backup_dir_uri", it.toString())
            }
            // 自动启用备份
            BackupManager.setupAutoBackup(context, true)
            autoBack.value = true
        } ?: run {
            // 用户取消选择时保持开关关闭
            autoBack.value = false
        }
    }


    val restoreLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            BackupManager.restoreBackup(context, it) { success ->
                Toast.makeText(
                    context,
                    if (success) "恢复成功" else "恢复失败",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    SettingItem(
        title = "数据管理",
        description = "数据导出及导入",
        onClick = { showList.value = true }
        ) {
        Icon(Icons.Default.ArrowDropDown, "展开")
    }
    if (showList.value){
        AlertDialog(
            onDismissRequest = { showList.value = false },
            title = { Text("导出/导入数据") },
            text = {
                Column (
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center
                ) {
                    // 导出按钮
                    TextButton(
                        onClick = {
                            if (BackupManager.hasBackupLocation(context)) {
                                BackupManager.createBackup(context) { success ->
                                    Toast.makeText(context, if (success) "备份成功" else "备份失败", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                backupDirLauncher.launch(null) // 触发目录选择
                            }
                        }
                    ) {
                        Text("导出数据")
                    }
                    // 导入按钮
                    TextButton(
                        onClick = {
                            restoreLauncher.launch(arrayOf("application/json"))
                            showList.value = false
                        }
                    ) {
                        Text("导入数据")
                    }
                    // 自动备份开关
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("自动备份")
                        Switch(
                            checked = autoBack.value,
                            onCheckedChange = { enabled ->
                                if (enabled) {
                                    if (BackupManager.hasBackupLocation(context)) {
                                        // 已有备份目录直接启用
                                        BackupManager.setupAutoBackup(context, true)
                                        autoBack.value = true
                                    } else {
                                        // 触发目录选择对话框
                                        backupDirLauncher.launch(null)
                                    }
                                } else {
                                    // 关闭自动备份
                                    BackupManager.setupAutoBackup(context, false)
                                    autoBack.value = false
                                }
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showList.value = false }) {
                    Text("关闭")
                }
            }
        )
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
        val context = LocalContext.current
        val sharedPreferences = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
        // 从 SharedPreferences 初始化目标
        val (dailyGoal, setDailyGoal) = remember {
            mutableStateOf(
                sharedPreferences.getInt("daily_goal", 8) // 默认8杯
            )
        }
        DailyGoal(
            currentGoal = dailyGoal,
            onGoalChanged = { newGoal ->
                setDailyGoal(newGoal)
                sharedPreferences.edit().putInt("daily_goal", newGoal).apply()
            }
        )
        HorizontalDivider(thickness = 2.dp)
        StyleSetting()
        HorizontalDivider(thickness = 2.dp)
        DataBackup()
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