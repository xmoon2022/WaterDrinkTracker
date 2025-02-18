package io.github.xmoon2022.water.ui.screen.settings.screens.data_setting.items

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import io.github.xmoon2022.water.R
import io.github.xmoon2022.water.ui.screen.settings.SettingItem
import io.github.xmoon2022.water.utils.BackupManager
import io.github.xmoon2022.water.utils.loadHistory
import io.github.xmoon2022.water.utils.saveHistory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

var errorMessage by mutableStateOf<String?>(null)

@Composable
fun HistoryDataChange(){
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
    val showDialog = remember { mutableStateOf(false) }
    var year by remember { mutableStateOf("") }
    var month by remember { mutableStateOf("") }
    var day by remember { mutableStateOf("") }
    var count by remember { mutableStateOf("") }

    val (yearFocus, monthFocus, dayFocus, countFocus) = remember { FocusRequester.createRefs() }
    SettingItem(
        title = "历史数据修改",
        description = "修改某天错误的数据",
        icon = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.history),
                contentDescription = null
            )
        },
        onClick = {showDialog.value = true}
    )
    LaunchedEffect(showDialog.value) {
        if (showDialog.value) {
            yearFocus.requestFocus()
        }
    }
    if (showDialog.value)
    {
        AlertDialog(
            onDismissRequest = { showDialog.value = true },
            title = { Text("修改历史数据") },
            text = {
                Column {
                    // 日期输入行
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = year,
                            onValueChange = { year = it },
                            label = { Text("年") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next // 设置为下一步
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { monthFocus.requestFocus() } // 回车触发
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(yearFocus)
                        )
                        OutlinedTextField(
                            value = month,
                            onValueChange = { month = it },
                            label = { Text("月") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { dayFocus.requestFocus() }
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(monthFocus)
                        )
                        OutlinedTextField(
                            value = day,
                            onValueChange = { day = it },
                            label = { Text("日") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { countFocus.requestFocus() }
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(dayFocus)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    // 杯数输入
                    OutlinedTextField(
                        value = count,
                        onValueChange = { count = it },
                        label = { Text("杯数") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done // 最后一个设置为完成
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { confirm(year, month, day, count, prefs, context, showDialog) }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(countFocus)
                    )
                    errorMessage?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirm(year, month, day, count, prefs, context, showDialog)
                    }
                ) {
                    Text("确认")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog.value = false }
                ) {
                    Text("取消")
                }
            },
            properties = DialogProperties(dismissOnClickOutside = false)
        )
    }
}

private fun confirm(
    year: String,
    month: String,
    day: String,
    count: String,
    prefs: SharedPreferences,
    context: Context,
    showDialog: MutableState<Boolean>
){
    // 验证输入
    val y = year.toIntOrNull()
    val m = month.toIntOrNull()
    val d = day.toIntOrNull()
    val c = count.toIntOrNull()
    when {
        y == null || m == null || d == null ->
            errorMessage = "请输入有效的日期"
        c == null ->
            errorMessage = "请输入有效的杯数"
        !isValidDate(y, m, d) ->
            errorMessage = "无效的日期"
        else -> {
            try {
                val date = LocalDate.of(y, m, d)
                val formatter = DateTimeFormatter.ISO_LOCAL_DATE
                val dateString = date.format(formatter)

                // 更新SharedPreferences
                val history = prefs.loadHistory().toMutableMap()
                history[dateString] = c
                prefs.saveHistory(history)
                Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show()
                showDialog.value = false
            } catch (e: Exception) {
                errorMessage = "日期保存失败: ${e.message}"
            }
        }
    }
}

private fun isValidDate(year: Int, month: Int, day: Int): Boolean {
    return try {
        LocalDate.of(year, month, day)
        true
    } catch (e: Exception) {
        false
    }
}