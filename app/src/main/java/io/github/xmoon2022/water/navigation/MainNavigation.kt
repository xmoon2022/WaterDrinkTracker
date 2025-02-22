package io.github.xmoon2022.water.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.github.xmoon2022.water.ui.screen.calendar.CalendarScreen
import io.github.xmoon2022.water.ui.screen.home.MainScreen
import io.github.xmoon2022.water.ui.screen.settings.SettingsScreen
import io.github.xmoon2022.water.ui.screen.settings.screens.data_setting.DataSettingScreen
import io.github.xmoon2022.water.ui.screen.settings.screens.style_setting.StyleSettingScreen

sealed class Screen(val route: String) {
    data object Home : Screen("main")
    data object Calendar : Screen("calendar")
    data object Settings : Screen("Settings")
    data object StyleSettings : Screen("setting_style")
    data object DataSettings : Screen("setting_data")
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val routesWithBottomBar = listOf(Screen.Home.route, Screen.Calendar.route, Screen.Settings.route)
    Scaffold(
        bottomBar = {
            if (currentRoute in routesWithBottomBar) {
                BottomBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "main",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { backStackEntry ->
                SwipeableScreen(
                    currentRoute = Screen.Home.route,
                    navController = navController
                ) {
                    MainScreen()
                }
            }
            composable(Screen.Calendar.route) { backStackEntry ->
                SwipeableScreen(
                    currentRoute = Screen.Calendar.route,
                    navController = navController
                ) {
                    CalendarScreen()
                }
            }
            composable(Screen.Settings.route) { backStackEntry ->
                SwipeableScreen(
                    currentRoute = Screen.Settings.route,
                    navController = navController
                ) {
                    SettingsScreen(navController)
                }
            }
            composable(Screen.StyleSettings.route) { StyleSettingScreen() }
            composable(Screen.DataSettings.route) { DataSettingScreen() }
        }
    }
}
@Composable
private fun SwipeableScreen(
    currentRoute: String,
    navController: NavController,
    content: @Composable () -> Unit
) {
    val routesOrder = listOf(Screen.Home.route, Screen.Calendar.route, Screen.Settings.route)
    val currentIndex = routesOrder.indexOf(currentRoute)

    if (currentIndex == -1) {
        content()
        return
    }

    val density = LocalDensity.current
    val swipeThreshold = with(density) { 80.dp.toPx() }
    var dragOffset by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { dragOffset = 0f },
                    onDragEnd = {
                        when {
                            // 向左滑动（负方向）
                            dragOffset < -swipeThreshold -> {
                                val nextIndex = (currentIndex + 1) % routesOrder.size
                                navigateWithBottomNavBehavior(
                                    navController,
                                    routesOrder[nextIndex]
                                )
                            }
                            // 向右滑动（正方向）
                            dragOffset > swipeThreshold -> {
                                val prevIndex = (currentIndex - 1 + routesOrder.size) % routesOrder.size
                                navigateWithBottomNavBehavior(
                                    navController,
                                    routesOrder[prevIndex]
                                )
                            }
                        }
                        dragOffset = 0f
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        dragOffset += dragAmount
                    }
                )
            }
    ) {
        content()
    }
}

private fun navigateWithBottomNavBehavior(
    navController: NavController,
    route: String
) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
