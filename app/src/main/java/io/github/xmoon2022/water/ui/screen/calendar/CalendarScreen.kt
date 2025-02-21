package io.github.xmoon2022.water.ui.screen.calendar

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.xmoon2022.water.ui.screen.calendar.items.BottomWaterDataView
import io.github.xmoon2022.water.ui.screen.calendar.items.CalendarGrid
import io.github.xmoon2022.water.ui.screen.calendar.items.TopDateBar
import io.github.xmoon2022.water.ui.screen.calendar.model.CalendarViewModel
import io.github.xmoon2022.water.ui.theme.WaterTheme
import java.time.LocalDate

@Composable
fun CalendarScreen() {
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }
    val viewModel: CalendarViewModel = viewModel(
        factory = remember {
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CalendarViewModel(sharedPreferences) as T
                }
            }
        }
    )
    // 使用完整日期作为主状态
    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(currentDate) }

    Column {
        TopDateBar(
            currentDate = currentDate,
            onDateSelected = { newDate ->
                currentDate = newDate
                selectedDate = null
            }
        )

        CalendarGrid(
            baseDate = currentDate,
            selectedDate = selectedDate,
            onDateClick = { date -> selectedDate = date },
            viewModel = viewModel // 传递 ViewModel
        )

        BottomWaterDataView(
            selectedDate = selectedDate,
            viewModel = viewModel
        )
    }
}

@Preview(showBackground = true,showSystemUi = true)
@Composable
fun Preview(){
    WaterTheme {
        CalendarScreen()
    }
}