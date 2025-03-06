package io.github.xmoon2022.water.ui.screen.settings.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.xmoon2022.water.R
import io.github.xmoon2022.water.navigation.Screen
import io.github.xmoon2022.water.ui.screen.settings.SettingItem

@Composable
fun AboutSetting(navController: NavController) {
    SettingItem(
        title = "关于",
        icon = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.about),
                contentDescription = "关于图标",
                modifier = Modifier.size(24.dp)
            )
        },
        onClick = { navController.navigate(Screen.AboutSettings.route) }
    ){
        Column {
            Text("版本号：${stringResource(id = R.string.version)}", color = Color.Gray)
            Text("开发者：xmoon", color = Color.Gray)
        }
    }
}