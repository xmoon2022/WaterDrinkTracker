package io.github.xmoon2022.water.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.github.xmoon2022.water.ui.screen.calendar.CalendarScreen
import io.github.xmoon2022.water.ui.screen.calendar.items.CalendarFabButton
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
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        floatingActionButtonPosition = FabPosition.End,
        bottomBar = {
            if (currentRoute in routesWithBottomBar) {
                BottomBar(navController = navController)
            }
        },
        floatingActionButton = {
            if (currentRoute == Screen.Calendar.route) {
                CalendarFabButton(snackbarHostState = snackbarHostState)
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "main",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { MainScreen() }
            composable(Screen.Calendar.route) { CalendarScreen() }
            composable(Screen.Settings.route) { SettingsScreen(navController) }
            composable(Screen.StyleSettings.route) { StyleSettingScreen() }
            composable(Screen.DataSettings.route) { DataSettingScreen() }
        }
    }
}