package io.github.xmoon2022.water.ui.screen.calendar.items

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.github.xmoon2022.water.ui.screen.calendar.model.CalendarViewModel
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun MonthlyChart(viewModel: CalendarViewModel, baseDate: LocalDate) {
    val monthlyData = remember(baseDate) {
        viewModel.dailyCounts.filter {
            YearMonth.from(LocalDate.parse(it.key)) == YearMonth.from(baseDate)
        }
    }

//    Column {
//        Text("本月饮水趋势", style = MaterialTheme.typography.titleMedium)
//        BarChart(
//            data = monthlyData.map { it.value.toFloat() },
//            // 配置颜色、轴标签等...
//        )
//    }
}