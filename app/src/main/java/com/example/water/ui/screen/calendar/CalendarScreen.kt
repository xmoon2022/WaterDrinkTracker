package com.example.water.ui.screen.calendar

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.water.ui.screen.calendar.items.BottomWaterDataView
import com.example.water.ui.screen.calendar.items.CalendarGrid
import com.example.water.ui.screen.calendar.items.TopDateBar
import com.example.water.ui.theme.waterTheme
import java.time.LocalDate

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

@Preview(showBackground = true,showSystemUi = true)
@Composable
fun Preview(){
    waterTheme {
        CalendarScreen()
    }
}