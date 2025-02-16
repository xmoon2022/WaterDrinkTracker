package io.github.xmoon2022.water.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.xmoon2022.water.ui.screen.calendar.CalendarScreen
import io.github.xmoon2022.water.ui.screen.home.MainScreen
import io.github.xmoon2022.water.ui.screen.settings.SettingsScreen

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomBar(navController = navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "main",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("main") { MainScreen() }
            composable("calendar") { CalendarScreen() }
            composable("Settings") { SettingsScreen() }
        }
    }
}