package com.example.water.ui.widget.action

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.updateAll
import com.example.water.ui.widget.GlanceWidget
import com.example.water.utils.DateUtils
import com.example.water.utils.getTodayCount
import com.example.water.utils.saveTodayCount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DecrementAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        try {
            val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            // 检查日期重置
            DateUtils.checkDailyReset(prefs)
            // 修改计数（不低于 0）
            val current = prefs.getTodayCount()
            val newCount = maxOf(current - 1, 0)
            prefs.saveTodayCount(newCount)
            // 更新 Widget
            // 使用 withContext 确保在主线程更新
            withContext(Dispatchers.Main) {
                GlanceWidget().updateAll(context)
            }
        } catch (e: Exception) {
            Log.e("Widget", "Decrement Error", e)
        }
    }
}