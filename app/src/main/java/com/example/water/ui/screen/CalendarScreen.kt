package com.example.water.ui.screen

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.water.ui.theme.waterTheme
import com.example.water.utils.loadHistory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.DateTimeException
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
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

// 月份选择对话框
@RequiresApi(Build.VERSION_CODES.O)
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

data class CalendarDate(
    val date: LocalDate,      // 具体日期
    val isCurrentMonth: Boolean // 是否属于当前月
)

@RequiresApi(Build.VERSION_CODES.O)
fun generateCalendarDates(baseDate: LocalDate): List<CalendarDate> {
    val firstDayOfMonth = baseDate.withDayOfMonth(1)
    val startOfWeek = DayOfWeek.MONDAY // 假设周起始日为周日
    // 计算当月的第一天是周几的偏移量
    val dayOfWeek = firstDayOfMonth.dayOfWeek
    val offset = (dayOfWeek.value - startOfWeek.value + 7) % 7
    // 生成上个月补白的日期
    val prevMonthDates = (1..offset).map { offsetDay ->
        val date = firstDayOfMonth.minusDays(offset - offsetDay.toLong() + 1)
        CalendarDate(date, false)
    }

    // 生成当前月的所有日期
    val currentMonthDates = (1..baseDate.lengthOfMonth()).map { day ->
        val date = firstDayOfMonth.withDayOfMonth(day)
        CalendarDate(date, true)
    }

    // 计算需要补足的下个月天数
    val totalDays = prevMonthDates.size + currentMonthDates.size
    val nextMonthPadding = (7 - (totalDays % 7)) % 7

    // 生成下个月补白的日期
    val nextMonthStart = firstDayOfMonth.plusMonths(1)
    val nextMonthDates = (0 until nextMonthPadding).map { day ->
        val date = nextMonthStart.plusDays(day.toLong())
        CalendarDate(date, false)
    }

    return prevMonthDates + currentMonthDates + nextMonthDates
}

@Composable
fun WeekDaysRow() {
    // 固定中文星期简写（若需国际化可用 LocalDate 解析）
    val weekDays = listOf("一", "二", "三", "四", "五", "六", "日")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp) // 与日期单元格对齐
    ) {
        weekDays.forEach { day ->
            Box(
                modifier = Modifier
                    .weight(1f)          // 与网格列宽一致
                    .aspectRatio(1f)     // 保持正方形
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
// 更新 CalendarGrid 参数
@Composable
fun CalendarGrid(
    baseDate: LocalDate,
    selectedDate: LocalDate?,
    onDateClick: (LocalDate) -> Unit,
    sharedPreferences: SharedPreferences
) {
    val calendarDates = remember(baseDate) {
        generateCalendarDates(baseDate)
    }

    // 高亮有数据的日期
    val markedDates = remember(baseDate) {
        val historyJson = sharedPreferences.getString("daily_counts", "{}")
        val historyType = object : TypeToken<Map<String, Int>>() {}.type
        Gson().fromJson<Map<String, Int>>(historyJson, historyType)?.keys ?: emptySet()
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        WeekDaysRow()
        LazyVerticalGrid(columns = GridCells.Fixed(7)) {
            items(calendarDates) { calendarDate ->
                val isMarked = markedDates.contains(
                    calendarDate.date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                )

                CalendarDayCell(
                    calendarDate = calendarDate,
                    isSelected = calendarDate.date == selectedDate,
                    isMarked = isMarked,
                    isToday = calendarDate.date == LocalDate.now(),
                    onClick = {
                        if (calendarDate.isCurrentMonth) {
                            onDateClick(calendarDate.date)
                        }
                    }
                )
            }
        }
    }
}

// 更新 CalendarDayCell
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarDayCell(
    calendarDate: CalendarDate,
    isSelected: Boolean,
    isMarked: Boolean,
    isToday: Boolean,  // 新增参数
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clickable(
                enabled = calendarDate.isCurrentMonth,
                onClick = onClick
            )
            // 背景色优先级：选中 > 当天 > 当前月
            .background(
                color = when {
                    isSelected -> Color.Blue.copy(alpha = 0.3f)
                    calendarDate.isCurrentMonth -> Color.LightGray.copy(alpha = 0.3f)
                    else -> Color.Transparent
                },
                shape = CircleShape
            )
            // 边框优先级：选中 > 当天
            .border(
                width = when {
                    isSelected -> 2.dp
                    else -> 0.dp
                },
                color = when {
                    isSelected -> Color.Blue
                    else -> Color.Transparent
                },
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        // 日期数字
        Text(
            text = calendarDate.date.dayOfMonth.toString(),
            color = when {
                isSelected -> Color.Blue
                isToday -> Color.Green  // 当天文字颜色
                calendarDate.isCurrentMonth -> Color.Black
                else -> Color.Gray
            },
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
        )

        // 数据标记点（显示在当天标识下方）
        if (isMarked && !isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(6.dp)
                    .background(Color.Blue, CircleShape)
                    .padding(bottom = 4.dp)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BottomWaterDataView(selectedDate: LocalDate?, sharedPreferences: SharedPreferences) {
    // 从 SharedPreferences 获取喝水数据
    val (cups, formattedDate) = remember(selectedDate) {
        if (selectedDate != null) {
            val dateStr = selectedDate.toString() // 直接使用 ISO 格式
            val history = sharedPreferences.loadHistory()
            val cups = history[dateStr] ?: 0
            Pair(cups, selectedDate.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")))
        } else {
            Pair(0, "")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (selectedDate != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // 日期标题
                Text(
                    text = "📅 $formattedDate",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(Modifier.height(8.dp))

                // 喝水数据展示
                if (cups > 0) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // 杯数文字
                        Text(
                            text = buildAnnotatedString {
                                append("今日完成喝水 ")
                                withStyle(style = SpanStyle(color = Color.Blue)) {
                                    append("$cups")
                                }
                                append(" 杯")
                            },
                            fontSize = 16.sp
                        )

                        // 杯子表情展示
                        Row(
                            modifier = Modifier.padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            repeat(cups) {
                                Text("🥛", fontSize = 24.sp)
                            }
                        }
                    }
                } else {
                    Text(
                        text = "当日没有喝水记录",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            }
        } else {
            Text(
                text = "点击日期查看喝水记录 →",
                color = Color.Gray,
                fontSize = 16.sp
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen() {
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }
    // 使用完整日期作为主状态
    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    Column {
        TopDateBar(
            currentDate = currentDate,
            onDateSelected = { newDate ->
                currentDate = newDate
                selectedDate = null
            }
        )

        CalendarGrid(
            baseDate = currentDate, // 改为基于具体日期生成网格
            selectedDate = selectedDate,
            onDateClick = { date -> selectedDate = date },
            sharedPreferences = sharedPreferences
        )

        BottomWaterDataView(
            selectedDate = selectedDate,
            sharedPreferences = sharedPreferences
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true,showSystemUi = true)
@Composable
fun Preview(){
    waterTheme {
        CalendarScreen()
    }
}