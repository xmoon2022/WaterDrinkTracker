package io.github.xmoon2022.water.ui.screen.settings.screens.style_setting.items

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.xmoon2022.water.ui.screen.settings.SettingItem


// 主题选项枚举
enum class ThemeOption(val value: String) {
    SYSTEM("system"),
    LIGHT("light"),
    DARK("dark")
}

@Composable
fun ChooseTheme() {
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }

    // 从SharedPreferences读取保存的主题
    val savedTheme = sharedPreferences.getString("theme", ThemeOption.SYSTEM.value)
    var selectedTheme by remember {
        mutableStateOf(ThemeOption.entries.first { it.value == savedTheme })
    }

    var showDialog by remember { mutableStateOf(false) }

    // 主题选择对话框
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("选择主题") },
            text = {
                Column {
                    ThemeOption.entries.forEach { theme ->
                        Row(
                            Modifier
                                .selectable(
                                    selected = (theme == selectedTheme),
                                    onClick = {
                                        selectedTheme = theme
                                        // 立即保存选择
                                        sharedPreferences.edit()
                                            .putString("theme", theme.value)
                                            .apply()
                                    }
                                )
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (theme == selectedTheme),
                                onClick = null
                            )
                            Text(
                                text = when (theme) {
                                    ThemeOption.SYSTEM -> "跟随系统"
                                    ThemeOption.LIGHT -> "亮色主题"
                                    ThemeOption.DARK -> "暗色主题"
                                },
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("确定")
                }
            }
        )
    }

    // 设置项组件
    SettingItem(
        title = "应用主题",
        description = when (selectedTheme) {
            ThemeOption.SYSTEM -> "跟随系统"
            ThemeOption.LIGHT -> "亮色主题"
            ThemeOption.DARK -> "暗色主题"
        },
        onClick = { showDialog = true }
    )
}