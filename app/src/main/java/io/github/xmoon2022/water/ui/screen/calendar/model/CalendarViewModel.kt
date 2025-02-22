package io.github.xmoon2022.water.ui.screen.calendar.model

import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CalendarViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {
    private var _dailyCounts by mutableStateOf<Map<String, Int>>(emptyMap())
    val dailyCounts: Map<String, Int> get() = _dailyCounts
    val markedDates: Set<String> get() = _dailyCounts.keys
    private var _dailyGoal by mutableIntStateOf(8)
    val dailyGoal: Int get() = _dailyGoal
    init {
        viewModelScope.launch {
            loadData()
        }
    }

    private suspend fun loadData() {
        withContext(Dispatchers.IO) {
            val historyJson = sharedPreferences.getString("daily_counts", "{}") ?: "{}"
            val type = object : TypeToken<Map<String, Int>>() {}.type
            _dailyCounts = Gson().fromJson(historyJson, type) ?: emptyMap()
            _dailyGoal = sharedPreferences.getInt("daily_goal", 8)
        }
    }
}