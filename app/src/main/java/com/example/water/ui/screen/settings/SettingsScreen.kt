package com.example.water.ui.screen.settings

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.water.ui.screen.settings.items.DailyGoal
import com.example.water.ui.screen.settings.items.DataBackup
import com.example.water.ui.screen.settings.items.StyleSetting
import com.example.water.ui.screen.settings.items.about
import com.example.water.ui.theme.waterTheme

@Composable
fun SettingsScreen(){
    Column {
        DailyGoal()
        HorizontalDivider(thickness = 2.dp)
        StyleSetting()
        HorizontalDivider(thickness = 2.dp)
        DataBackup()
        HorizontalDivider(thickness = 2.dp)
        about()
    }
}

@Composable
@Preview(showBackground = true,showSystemUi = true)
fun SettingPreview(){
    waterTheme {
        SettingsScreen()
    }
}