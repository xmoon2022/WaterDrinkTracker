package io.github.xmoon2022.water.ui.screen.calendar.items

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.xmoon2022.water.utils.loadHistory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
                                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
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