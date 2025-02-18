package io.github.xmoon2022.water.ui.screen.calendar.items

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class CalendarDate(
    val date: LocalDate,      // 具体日期
    val isCurrentMonth: Boolean // 是否属于当前月
)

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
                    isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    calendarDate.isCurrentMonth -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
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
                    isSelected -> MaterialTheme.colorScheme.primary
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
                isSelected -> MaterialTheme.colorScheme.inversePrimary
                isToday -> MaterialTheme.colorScheme.inversePrimary  // 当天文字颜色
                calendarDate.isCurrentMonth -> MaterialTheme.colorScheme.onSurface
                else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            },
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
        )

        // 数据标记点（显示在当天标识下方）
        if (isMarked && !isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(6.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .padding(bottom = 4.dp)
            )
        }
    }
}