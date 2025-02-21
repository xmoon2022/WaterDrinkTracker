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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.xmoon2022.water.ui.screen.calendar.model.CalendarViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

data class CalendarDate(
    val date: LocalDate,      // 具体日期
    val isCurrentMonth: Boolean // 是否属于当前月
)

fun generateCalendarDates(baseDate: LocalDate): List<CalendarDate> {
    val yearMonth = YearMonth.from(baseDate)
    val firstDay = yearMonth.atDay(1)
    val lastDay = yearMonth.atEndOfMonth()

    val startDate = firstDay.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val endDate = lastDay.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

    return generateSequence(startDate) { it.plusDays(1) }
        .takeWhile { !it.isAfter(endDate) }
        .map { CalendarDate(it, YearMonth.from(it) == yearMonth) }
        .toList()
}

// 更新 CalendarGrid 参数
@Composable
fun CalendarGrid(
    baseDate: LocalDate,
    selectedDate: LocalDate?,
    onDateClick: (LocalDate) -> Unit,
    viewModel: CalendarViewModel // 接收 ViewModel
) {
    val calendarDates = remember(baseDate) {
        generateCalendarDates(baseDate)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        WeekDaysRow()
        LazyVerticalGrid(columns = GridCells.Fixed(7)) {
            items(
                items = calendarDates,
                key = { it.date.toString() }
            ) { calendarDate ->
                val isMarked = viewModel.markedDates.contains(
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