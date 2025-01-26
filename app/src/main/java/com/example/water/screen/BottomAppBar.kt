package com.example.water.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 统一控制参数
    val iconSize = 24.dp // 通过修改这个值统一调整图标大小
    val bottomBarHeight = 100.dp // 底部栏总高度
    val iconButtonSize = 60.dp // 图标按钮点击区域

    BottomAppBar(
        modifier = Modifier.height(bottomBarHeight),
        contentPadding = PaddingValues(0.dp) // 移除默认内边距
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = bottomBarHeight),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically // 垂直居中
        ) {
            listOf(
                Triple("main", Icons.Default.Home, "首页"),
                Triple("calendar", Icons.Default.DateRange, "日历"),
                Triple("Settings", Icons.Default.Settings, "设置")
            ).forEach { (route, icon, desc) ->
                IconButton(
                    onClick = {
                        navController.navigate(route) {
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.size(iconButtonSize) // 控制点击区域大小
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = desc,
                        modifier = Modifier.size(iconSize),
                        tint = if (currentRoute == route) Color.Blue else Color.Gray
                    )
                }
            }
        }
    }
}