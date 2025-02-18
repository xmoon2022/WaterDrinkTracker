package io.github.xmoon2022.water.ui.screen.settings.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import io.github.xmoon2022.water.R
import io.github.xmoon2022.water.ui.screen.settings.SettingItem

@Composable
fun About(){
    SettingItem(
        title = "关于",
        icon = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.about),
                contentDescription = "关于图标",
                modifier = Modifier.size(24.dp)
            )
        },
    ) {
        Column {
//                ClickableText(
//                    text = AnnotatedString("隐私政策"),
//                    onClick = { /* 打开网页 */ }
//                )
            Text("版本号：1.0.1", color = Color.Gray)
            Text("开发者：xmoon", color = Color.Gray)
        }
    }
}