package io.github.xmoon2022.water.ui.screen.home.style

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.updateAll
import io.github.xmoon2022.water.R
import io.github.xmoon2022.water.utils.DateUtils
import io.github.xmoon2022.water.utils.getTodayCount
import io.github.xmoon2022.water.utils.saveTodayCount
import io.github.xmoon2022.water.widget.NarrowWidget
import io.github.xmoon2022.water.widget.WideWidget

@Composable
fun InteractiveWaterCard(target: Int) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
    var current by remember { mutableIntStateOf(prefs.getTodayCount().coerceAtMost(target)) }

    // 监听SharedPreferences的变化
    val listener = remember {
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "daily_counts") {
                current = prefs.getTodayCount().coerceAtMost(target)
            }
        }
    }

    DisposableEffect(Unit) {
        prefs.registerOnSharedPreferenceChangeListener(listener)
        onDispose {
            prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    LaunchedEffect(Unit) {
        DateUtils.checkDailyReset(prefs)
        current = prefs.getTodayCount().coerceAtMost(target)
    }

    LaunchedEffect(current) {
        prefs.saveTodayCount(current)
        WideWidget().updateAll(context)
        NarrowWidget().updateAll(context)
    }

    val customCardColors = CardDefaults.cardColors(
        contentColor = MaterialTheme.colorScheme.primary,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        disabledContentColor = MaterialTheme.colorScheme.surface,
        disabledContainerColor = MaterialTheme.colorScheme.onSurface,
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Center
    ) {
        Card(
            modifier = Modifier
                .width(280.dp)
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = customCardColors
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "今日饮水",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(24.dp))
                WaterProgress(current, target)
                Spacer(Modifier.height(16.dp))
                ControlButtons(current, target, onValueChange = { newValue ->
                    current = newValue
                })
            }
        }
    }
}

@Composable
private fun ControlButtons(
    current: Int,
    target: Int,
    onValueChange: (Int) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            onClick = {
                onValueChange((current - 1).coerceAtLeast(0))
            },
            modifier = Modifier
                .padding(8.dp)
                .clip(CircleShape)
        ) {
            Icon(Icons.Default.KeyboardArrowDown, "Decrease")
        }
        Text(
            "${current}杯",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        IconButton(
            onClick = {
                if (current < target) {
                    onValueChange(current + 1)
                }
            },
            modifier = Modifier
                .padding(8.dp)
                .clip(CircleShape)
        ) {
            Icon(Icons.Default.KeyboardArrowUp, "Increase")
        }
    }
}

@Composable
fun WaterProgress(current: Int, target: Int) {
    // 进度动画（带弹性效果）
    val animatedProgress by animateFloatAsState(
        targetValue = current.toFloat() / target,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = Spring.StiffnessLow
        ),
        label = "progress"
    )

    // 颜色动画（当达到目标时变绿）
    val targetColor = if (current >= target) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(800),
        label = "waterColor"
    )

    // 水滴缩放动画（数值变化时跳动）
    val scale = remember { Animatable(1f) }
    LaunchedEffect(current) {
        if (current > 0) {
            scale.animateTo(
                targetValue = 1.2f,
                animationSpec = tween(150)
            )
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(150)
            )
        }
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Center) {
            CircularProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .size(200.dp),
                color = animatedColor,
                strokeWidth = 8.dp
            )
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.water),
                contentDescription = null,
                tint = animatedColor,
                modifier = Modifier
                    .size(80.dp)
                    .scale(scale.value)
            )
        }
    }
}