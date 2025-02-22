package io.github.xmoon2022.water.ui.screen.calendar

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CalendarScreen() {
    var slideDirection by remember { mutableIntStateOf(0) } // -1左滑，1右滑，0无
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
            },
            onSlideDirectionChanged = { direction ->
                slideDirection = direction // 更新 slideDirection
            }
        )

        AnimatedContent(
            targetState = currentDate,
            transitionSpec = {
                val direction = slideDirection
                slideInHorizontally(
                    initialOffsetX = { if (direction == 1) it else -it },
                    animationSpec = tween(200, easing = FastOutSlowInEasing)
                ) with
                        slideOutHorizontally(
                            targetOffsetX = { if (direction == 1) -it else it },
                            animationSpec = tween(200, easing = FastOutSlowInEasing)
                        )
            },
            label = "CalendarAnimation"
        ) { targetDate ->
            CalendarGrid(
                baseDate = targetDate,
                selectedDate = selectedDate,
                onDateClick = { date ->
                    val newDirection = if (date > currentDate) 1 else -1
                    slideDirection = newDirection
                    selectedDate = date
                    currentDate = date
                },
                viewModel = viewModel // 传递 ViewModel
            )
        }

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