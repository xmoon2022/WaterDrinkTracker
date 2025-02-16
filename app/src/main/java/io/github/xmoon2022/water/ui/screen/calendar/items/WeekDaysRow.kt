package io.github.xmoon2022.water.ui.screen.calendar.items

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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