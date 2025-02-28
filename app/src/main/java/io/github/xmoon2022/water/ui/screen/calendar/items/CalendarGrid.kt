package io.github.xmoon2022.water.ui.screen.calendar.items

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProgressIndicatorDefaults
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.xmoon2022.water.ui.screen.calendar.model.CalendarViewModel
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
        LazyVerticalGrid(columns = GridCells.Fixed(7)) {
            items(
                items = calendarDates,
                key = { it.date.toString() }
            ) { calendarDate ->
                val dateStr = calendarDate.date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                val currentCount = viewModel.dailyCounts[dateStr]
                val dailyGoal = viewModel.dailyGoal

                CalendarDayCell(
                    calendarDate = calendarDate,
                    isSelected = calendarDate.date == selectedDate,
                    currentCount = currentCount,  // 新增参数
                    dailyGoal = dailyGoal,
                    isToday = calendarDate.date == LocalDate.now(),
                    viewModel = viewModel,
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
    currentCount: Int?,
    dailyGoal: Int,
    isToday: Boolean,
    viewModel: CalendarViewModel,
    onClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var tempCount by remember { mutableStateOf(currentCount?.toString() ?: "") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .padding(4.dp)
                .pointerInput(calendarDate.isCurrentMonth) {
                    if (calendarDate.isCurrentMonth) {
                        detectTapGestures(
                            onLongPress = { showMenu = true },
                            onTap = { onClick() }
                        )
                    }
                }
                // 背景色优先级：选中 > 当天 > 当前月
                .background(
                    color = when {
                        isSelected -> MaterialTheme.colorScheme.inversePrimary
                        calendarDate.isCurrentMonth -> MaterialTheme.colorScheme.surfaceVariant
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
                    //isSelected -> MaterialTheme.colorScheme.inversePrimary
                    isToday -> MaterialTheme.colorScheme.tertiary // 当天文字颜色
                    calendarDate.isCurrentMonth -> MaterialTheme.colorScheme.onSurface
                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                },
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
            )

            // 进度条（覆盖在文字底部）
            if (calendarDate.isCurrentMonth && currentCount != null) {
                val progress = currentCount.toFloat() / dailyGoal
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(42.dp),
                    color = if (progress >= 1f) Color.Green else MaterialTheme.colorScheme.primary,
                    strokeWidth = 2.dp,
                    trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                )
            }
        }
        // 第一步：下拉菜单
        if (showMenu && calendarDate.isCurrentMonth) {
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("修改记录") },
                    onClick = {
                        showMenu = false
                        showEditDialog = true
                        tempCount = currentCount?.toString() ?: ""
                    }
                )
            }
        }
    }

    // 第二步：编辑对话框
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = {
                Text(
                    text = "修改 ${calendarDate.date.monthValue}月${calendarDate.date.dayOfMonth}日记录",
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = tempCount,
                        onValueChange = { tempCount = it },
                        label = { Text("饮水杯数") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "输入0可清除记录",
                        color = MaterialTheme.colorScheme.outline,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val count = tempCount.toIntOrNull()?.coerceAtLeast(0) ?: 0
                        viewModel.updateDailyCount(calendarDate.date, count)
                        showEditDialog = false
                    }
                ) { Text("确认") }
            },
            dismissButton = {
                TextButton(
                    onClick = { showEditDialog = false }
                ) { Text("取消") }
            }
        )
    }
}