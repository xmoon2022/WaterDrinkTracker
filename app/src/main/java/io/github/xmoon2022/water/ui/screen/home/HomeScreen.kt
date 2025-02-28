package io.github.xmoon2022.water.ui.screen.home

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.xmoon2022.water.ui.screen.home.style.CheckList
import io.github.xmoon2022.water.ui.screen.home.style.InteractiveWaterCard
import io.github.xmoon2022.water.ui.screen.settings.screens.style_setting.items.DisplayStyle
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
    val dailyGoal = sharedPreferences.getInt("daily_goal", 8)
    val displayStyle = remember { mutableStateOf(sharedPreferences.getString("display_style", DisplayStyle.WATER.name) ?: DisplayStyle.WATER.name) }

    DisposableEffect(sharedPreferences) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "display_style") {
                displayStyle.value = sharedPreferences.getString(key, DisplayStyle.WATER.name) ?: DisplayStyle.WATER.name
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        onDispose {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    val displayStyleName = displayStyle.value
    val selectedStyle = try {
        DisplayStyle.valueOf(displayStyleName)
    } catch (e: IllegalArgumentException) {
        sharedPreferences.edit().putString("display_style", DisplayStyle.WATER.name).apply()
        DisplayStyle.WATER
    }

    when (selectedStyle) {
        DisplayStyle.WATER -> InteractiveWaterCard(dailyGoal)
        DisplayStyle.CHECKLIST -> CheckList(dailyGoal)
    }
}