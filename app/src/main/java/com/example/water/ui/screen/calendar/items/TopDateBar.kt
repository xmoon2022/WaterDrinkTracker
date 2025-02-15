package com.example.water.ui.screen.calendar.items

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.DateTimeException
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Calendar

// 月份选择对话框
private fun showDatePicker(
    context: Context,
    initialDate: LocalDate,
    onConfirm: (LocalDate) -> Unit
) {
    val calendar = Calendar.getInstance().apply {
        set(initialDate.year, initialDate.monthValue - 1, initialDate.dayOfMonth)
    }

    DatePickerDialog(
        context,
        { _, year, month, day ->
            // 处理无效日期（如 2月30日）
            val adjustedDate = try {
                LocalDate.of(year, month + 1, day)
            } catch (e: DateTimeException) {
                // 自动修正为当月最后一天
                YearMonth.of(year, month + 1).atEndOfMonth()
            }
            onConfirm(adjustedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        // 保持完整日期选择功能
//        datePicker.minDate = LocalDate.now().minusYears(1).toEpochDay() // 可选：限制可选范围
//        datePicker.maxDate = LocalDate.now().plusYears(100).toEpochDay()
    }.show()
}

@Composable
fun TopDateBar(
    currentDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val context = LocalContext.current
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 上月按钮
        TextButton(
            onClick = {
                val newDate = currentDate.minusMonths(1)
                onDateSelected(newDate)
            },
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text("← 上月", fontSize = 14.sp)
        }

        // 日期+下拉按钮组合
        Row(
            modifier = Modifier
                .clickable { showDatePicker(context, currentDate, onDateSelected) }
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currentDate.format(dateFormatter),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "选择日期",
                modifier = Modifier.size(20.dp)
            )
        }

        // 下月按钮
        TextButton(
            onClick = {
                val newDate = currentDate.plusMonths(1)
                onDateSelected(newDate)
            },
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Text("下月 →", fontSize = 14.sp)
        }
    }
}