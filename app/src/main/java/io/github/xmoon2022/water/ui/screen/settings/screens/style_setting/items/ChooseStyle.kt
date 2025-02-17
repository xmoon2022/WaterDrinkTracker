package io.github.xmoon2022.water.ui.screen.settings.screens.style_setting.items

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.xmoon2022.water.ui.screen.settings.SettingItem

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
fun ChooseStyle() {
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