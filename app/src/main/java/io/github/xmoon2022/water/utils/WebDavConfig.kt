package io.github.xmoon2022.water.utils

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import io.github.xmoon2022.water.ui.screen.settings.SettingItem

@Composable
fun WebDavConfig(showDialog: MutableState<Boolean>) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
    var url by remember { mutableStateOf(prefs.getString("webdev_url", "")) }
    var username by remember { mutableStateOf(prefs.getString("webdev_user", "")) }
    var password by remember { mutableStateOf(prefs.getString("webdev_pwd", "")) }
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("配置 WebDev 服务") },
            text = {
                Column(Modifier.padding(vertical = 8.dp)) {
                    url?.let { it ->
                        OutlinedTextField(
                            value = it,
                            onValueChange = { url = it },
                            label = { Text("API 地址 (例如: https://api.webdev.com)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    username?.let {
                        OutlinedTextField(
                            value = it,
                            onValueChange = { username = it },
                            label = { Text("用户名") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        )
                    }
                    password?.let {
                        OutlinedTextField(
                            value = it,
                            onValueChange = { password = it },
                            label = { Text("密码") },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        prefs.edit()
                            .putString("webdev_url", url)
                            .putString("webdev_user", username)
                            .putString("webdev_pwd", password)
                            .apply()
                        Toast.makeText(context, "配置已保存", Toast.LENGTH_SHORT).show()
                        showDialog.value = false
                    },
                ) {
                    Text("保存")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog.value = false }) {
                    Text("取消")
                }
            }
        )
    }
}