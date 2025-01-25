package com.example.water.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomBar(navController: NavController){
    // 获取当前路由
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    BottomAppBar{
        Row (
            modifier = Modifier
                .fillMaxWidth(), // 让Row占据整个宽度
            horizontalArrangement = Arrangement.SpaceEvenly // 图标之间等距分布
        ) {
            IconButton(onClick = {
                    navController.navigate("main") {
                    // 避免重复压栈
                    launchSingleTop = true
                    }
                }
            ) {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "首页",
                    modifier = Modifier.size(32.dp)
                )
            }
            IconButton(onClick = {
                navController.navigate("calendar") {
                    launchSingleTop = true
                }
            }
            ) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = "日历",
                    modifier = Modifier.size(32.dp)
                )
            }
            IconButton(onClick = { /* do something */ }) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "设置",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}