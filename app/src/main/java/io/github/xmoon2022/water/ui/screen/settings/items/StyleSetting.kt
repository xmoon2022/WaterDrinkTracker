package io.github.xmoon2022.water.ui.screen.settings.items

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.xmoon2022.water.R
import io.github.xmoon2022.water.navigation.Screen
import io.github.xmoon2022.water.ui.screen.settings.SettingItem

@Composable
fun StyleSetting(navController: NavController) {
    SettingItem(
        title = "外观",
        description = "样式，暗色模式",
        icon = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.palette),
                contentDescription = "样式图标",
                modifier = Modifier.size(24.dp)
            )
        },
        onClick = { navController.navigate(Screen.StyleSettings.route) }
    )
}