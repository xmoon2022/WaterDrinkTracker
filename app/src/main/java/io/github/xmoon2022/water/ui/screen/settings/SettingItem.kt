package io.github.xmoon2022.water.ui.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.xmoon2022.water.ui.theme.WaterTheme

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
        Column{
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(16.dp)
            )
            content()
        }

}

@Composable
fun SettingItem(
    title: String,
    description: String? = null,
    icon: @Composable () -> Unit = {},
    onClick: () -> Unit = {},
    content: @Composable () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            icon()
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp, end = 8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
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
}

// 使用示例：
@Preview(showSystemUi = true)
@Composable
fun SettingsPreview() {
    WaterTheme{
        SettingsScreen {
            Text(
                text = "设置",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            SettingsSection(title = "账户设置") {
                Spacer(modifier = Modifier.height(8.dp))
                SettingItem(
                    title = "个人信息",
                    description = "管理您的个人资料",
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    onClick = { /* 处理点击 */ }
                )
                Spacer(modifier = Modifier.height(8.dp))
                SettingItem(
                    title = "安全设置",
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    onClick = { /* 处理点击 */ }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            SettingsSection(title = "通知设置") {
                SettingItem(
                    title = "消息通知",
                    content = { Switch(checked = true, onCheckedChange = {}) },
                    onClick = { /* 处理点击 */ }
                )
                Divider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
                SettingItem(
                    title = "推送频率",
                    description = "每天最多3次",
                    onClick = { /* 处理点击 */ }
                )
            }
        }
    }
}