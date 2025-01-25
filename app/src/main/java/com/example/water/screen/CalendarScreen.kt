package com.example.water.screen

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.water.ui.theme.waterTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TopYearMonthBar(currentDate: LocalDate) {
    val formattedDate = currentDate.format(
        DateTimeFormatter.ofPattern("yyyy年MM月")
    )
    // 3. 使用 Row 布局横向排列
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = formattedDate,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
    }
}

data class CalendarDate(
    val date: LocalDate,      // 具体日期
    val isCurrentMonth: Boolean // 是否属于当前月
)

@RequiresApi(Build.VERSION_CODES.O)
fun generateCalendarDates(currentDate: LocalDate): List<CalendarDate> {
    val firstDayOfMonth = currentDate.withDayOfMonth(1)
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
    val currentMonthDates = (1..currentDate.lengthOfMonth()).map { day ->
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
    currentDate: LocalDate,
    selectedDate: LocalDate?,
    onDateClick: (LocalDate) -> Unit
) {
    val calendarDates = remember(currentDate) { generateCalendarDates(currentDate) }

    Column(modifier = Modifier.fillMaxWidth()) {
        WeekDaysRow()
        LazyVerticalGrid(columns = GridCells.Fixed(7)) {
            items(calendarDates) { calendarDate ->
                CalendarDayCell(
                    calendarDate = calendarDate,
                    isSelected = calendarDate.date == selectedDate,
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
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
//            .weight(1f)
            .aspectRatio(1f)
            .padding(4.dp)
            .clickable(
                enabled = calendarDate.isCurrentMonth,
                onClick = onClick
            )
            .background(
                color = when {
                    isSelected -> Color.Blue.copy(alpha = 0.3f)
                    calendarDate.isCurrentMonth -> Color.LightGray.copy(alpha = 0.3f)
                    else -> Color.Transparent
                },
                shape = CircleShape
            )
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) Color.Blue else Color.Transparent,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = calendarDate.date.dayOfMonth.toString(),
            color = when {
                isSelected -> Color.Blue
                calendarDate.isCurrentMonth -> Color.Black
                else -> Color.Gray
            }
        )
    }
}

//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//fun CalendarDayCell(calendarDate: CalendarDate) {
//    Box(
//        modifier = Modifier
//            .aspectRatio(1f) // 保持正方形
//            .padding(4.dp)
//            .background(
//                color = if (calendarDate.isCurrentMonth) Color.LightGray.copy(alpha = 0.3f)
//                else Color.Transparent,
//                shape = CircleShape
//            ),
//        contentAlignment = Alignment.Center
//    ) {
//        Text(
//            text = calendarDate.date.dayOfMonth.toString(),
//            color = if (calendarDate.isCurrentMonth) Color.Black else Color.Gray,
//            fontSize = 16.sp
//        )
//    }
//}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BottomWaterDataView(selectedDate: LocalDate?, cups: Int) {
    val formattedDate = selectedDate?.format(
        DateTimeFormatter.ofPattern("yyyy-MM-dd")
    ) ?: ""

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
                    Text(
                        text = "当日喝水杯数：",
                        fontSize = 16.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(cups) {
                            Text("🥛", fontSize = 24.sp, modifier = Modifier.padding(2.dp))
                        }
                        Text(
                            text = "$cups 杯",
                            color = Color.Blue,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                } else {
                    Text(
                        text = "当日未记录喝水",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            }
        } else {
            Text("点击日期查看喝水记录", color = Color.Gray)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen() {
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("checklist_prefs", Context.MODE_PRIVATE)
    }
    val currentDate = remember { LocalDate.now() }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    // 获取选中日期的喝水杯数
    val selectedCups = remember(selectedDate) {
        selectedDate?.let { date ->
            val dateKey = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
            val historyJson = sharedPreferences.getString("daily_counts", "{}")
            val historyType = object : TypeToken<Map<String, Int>>() {}.type
            val history = Gson().fromJson<Map<String, Int>>(historyJson, historyType)
            history?.get(dateKey) ?: 0 // 无记录返回0
        } ?: 0
    }

//    Scaffold(
//        bottomBar = {
//            BottomBar()
//        }
//    ) { innerPadding ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize() // 让Box占据整个屏幕
//                .padding(innerPadding) // 使用Scaffold提供的内边距
//        ) {
            Column {
                TopYearMonthBar(currentDate)
                CalendarGrid(
                    currentDate = currentDate,
                    selectedDate = selectedDate,
                    onDateClick = { date -> selectedDate = date }
                )
                BottomWaterDataView(selectedDate, selectedCups)
            }
        //}
    //}
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true,showSystemUi = true)
@Composable
fun preview(){
    waterTheme {
        CalendarScreen()
    }
}