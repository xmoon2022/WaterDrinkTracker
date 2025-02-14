package com.example.water.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.water.ui.screen.BottomBar
import com.example.water.ui.screen.calendar.CalendarScreen
import com.example.water.ui.screen.home.MainScreen
import com.example.water.ui.screen.settings.SettingsScreen

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