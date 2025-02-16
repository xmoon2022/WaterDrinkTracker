package io.github.xmoon2022.water.ui.screen.settings.items

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.ImageProvider
import io.github.xmoon2022.water.R
import io.github.xmoon2022.water.ui.screen.settings.SettingItem
import io.github.xmoon2022.water.utils.BackupManager
import io.github.xmoon2022.water.utils.WebDavManager
import kotlinx.coroutines.launch


@Composable
fun CloudBackupItem() {
    val showDialog = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val isLoading = remember { mutableStateOf(false) }
    val context = LocalContext.current
    SettingItem(
        title = "云备份/恢复",
        description = "使用 WebDev 网盘同步数据",
        icon = {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.import_export),
                    contentDescription = "Cloud"
                )
               },
        onClick = { showDialog.value = true },
        content = {}
    )

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("WebDev 云备份") },
            text = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(
                        onClick = {
                            scope.launch {
                                isLoading.value = true
                                val success = WebDavManager.uploadBackup(context)
                                if (success)
                                    Toast.makeText(context, "上传成功", Toast.LENGTH_SHORT).show()
                                else
                                    Toast.makeText(context, "上传失败", Toast.LENGTH_SHORT).show()
                                isLoading.value = false
                                showDialog.value = false
                            }
                        }
                    ) {
                        if (isLoading.value) CircularProgressIndicator(Modifier.size(18.dp))
                        else Text("上传到云")
                    }

                    Button(
                        onClick = {
                            scope.launch {
                                isLoading.value = true
                                val success = WebDavManager.downloadBackup(context)
                                if (success)
                                    Toast.makeText(context, "恢复成功", Toast.LENGTH_SHORT).show()
                                else
                                    Toast.makeText(context, "恢复失败", Toast.LENGTH_SHORT).show()
                                isLoading.value = false
                                showDialog.value = false
                            }
                        }
                    ) {
                        if (isLoading.value) CircularProgressIndicator(Modifier.size(18.dp))
                        else Text("从云恢复")
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