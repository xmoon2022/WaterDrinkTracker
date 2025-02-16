package io.github.xmoon2022.water.utils

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate

object DateUtils {
    private const val LAST_SAVED_DATE_KEY = "last_saved_date"

    fun checkDailyReset(prefs: SharedPreferences) {
        val today = LocalDate.now().toString()
        val lastDate = prefs.getString(LAST_SAVED_DATE_KEY, null)

        if (lastDate != null && lastDate != today) {
            // 获取前一天的计数并保存到历史记录
            val history = prefs.loadHistory().toMutableMap()
            val previousCount = history[lastDate] ?: 0 // 直接获取前一天的计数
            history[lastDate] = previousCount // 确保前一天的计数不被覆盖
            prefs.saveHistory(history)

            // 重置当日数据为0
            prefs.saveTodayCount(0)
        }
        prefs.edit().putString(LAST_SAVED_DATE_KEY, today).apply()
    }
}

// 扩展函数统一SharedPreferences操作
fun SharedPreferences.loadHistory(): Map<String, Int> {
    return try {
        Gson().fromJson(getString("daily_counts", "{}"), object : TypeToken<Map<String, Int>>() {}.type)
            ?: emptyMap()
    } catch (e: Exception) {
        emptyMap()
    }
}

fun SharedPreferences.saveHistory(history: Map<String, Int>) {
    edit().putString("daily_counts", Gson().toJson(history)).apply()
}

fun SharedPreferences.getTodayCount(): Int {
    return loadHistory()[LocalDate.now().toString()] ?: 0
}

fun SharedPreferences.saveTodayCount(count: Int) {
    val history = loadHistory().toMutableMap()
    history[LocalDate.now().toString()] = count
    saveHistory(history)
}