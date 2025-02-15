package com.example.water.ui.screen.settings.items

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.example.water.R
import com.example.water.ui.screen.settings.SettingItem
import com.example.water.utils.BackupManager

@Composable
fun DataBackup(){
    val context = LocalContext.current
    val showList = remember { mutableStateOf(false) }
    val sharedPreferences = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
    val autoBack = remember {
        mutableStateOf(sharedPreferences.getBoolean("auto_back", false))
    }
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
        icon = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.backup),
                contentDescription = "数据图标",
                modifier = Modifier.size(24.dp)
            )
        },
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
                    // 自动备份按钮
                    TextButton(
                        onClick = {
                            if (BackupManager.hasBackupLocation(context)) {
                                // 已有备份目录直接启用
                                autoBack.value = !autoBack.value
                                sharedPreferences.edit().putBoolean("auto_back", autoBack.value).apply()
                                Toast.makeText(context, if (autoBack.value) "开启自动备份" else "关闭自动备份", Toast.LENGTH_SHORT).show()
                            } else {
                                // 触发目录选择对话框
                                backupDirLauncher.launch(null)
                            }
                        }
                    ) {
                        Text("自动备份")
                    }
                    // 自动备份开关
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(12.dp),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text("自动备份")
//                        Switch(
//                            checked = autoBack.value,
//                            onCheckedChange = { enabled ->
//                                if (enabled) {
//                                    if (BackupManager.hasBackupLocation(context)) {
//                                        // 已有备份目录直接启用
//                                        BackupManager.setupAutoBackup(context, true)
//                                        autoBack.value = true
//                                        sharedPreferences.edit().putBoolean("auto_back", true).apply()
//                                    } else {
//                                        // 触发目录选择对话框
//                                        backupDirLauncher.launch(null)
//                                    }
//                                } else {
//                                    // 关闭自动备份
//                                    BackupManager.setupAutoBackup(context, false)
//                                    autoBack.value = false
//                                    sharedPreferences.edit().putBoolean("auto_back", false).apply()
//                                }
//                            }
//                        )
//                    }
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