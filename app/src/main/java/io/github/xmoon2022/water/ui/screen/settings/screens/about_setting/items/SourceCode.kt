package io.github.xmoon2022.water.ui.screen.settings.screens.about_setting.items

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import io.github.xmoon2022.water.R
import io.github.xmoon2022.water.ui.screen.settings.SettingItem

@Composable
fun SourceCode(){
    val showCodeDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current
    SettingItem(
        title = "源代码",
        icon = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.code),
                contentDescription = "源代码图标",
                modifier = Modifier.size(24.dp)
            )
        },
        onClick = { showCodeDialog.value = true },
        content = {}
    )
    if (showCodeDialog.value){
        AlertDialog(
            onDismissRequest = { showCodeDialog.value = false },  // 点击外部或返回键关闭
            title = { Text(text = "打开源代码") },
            text = { Text(text = "即将跳转到:\nhttps://github.com/xmoon2022/WaterDrinkTracker") },
            confirmButton = {
                TextButton(
                    onClick = {
                        // 跳转到浏览器
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("https://github.com/xmoon2022/WaterDrinkTracker")
                        }
                        context.startActivity(intent)
                        showCodeDialog.value = false  // 关闭对话框
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showCodeDialog.value = false }  // 关闭对话框
                ) {
                    Text("取消")
                }
            }
        )
    }
}