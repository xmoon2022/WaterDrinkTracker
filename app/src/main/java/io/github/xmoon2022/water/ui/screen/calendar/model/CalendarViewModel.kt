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
import java.time.LocalDate

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

    fun updateDailyCount(date: LocalDate, count: Int) {
        val dateKey = date.toString()
        val newMap = _dailyCounts.toMutableMap().apply {
            if (count > 0) {
                this[dateKey] = count
            } else {
                remove(dateKey)
            }
        }
        _dailyCounts = newMap

        viewModelScope.launch(Dispatchers.IO) {
            sharedPreferences.edit()
                .putString("daily_counts", Gson().toJson(newMap))
                .apply()
        }
    }
}