package io.github.xmoon2022.water.widget.action

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.updateAll
import io.github.xmoon2022.water.utils.DateUtils
import io.github.xmoon2022.water.utils.getTodayCount
import io.github.xmoon2022.water.utils.saveTodayCount
import io.github.xmoon2022.water.widget.NarrowWidget
import io.github.xmoon2022.water.widget.WideWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class IncrementAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        try {
            val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val dailyGoal = prefs.getInt("daily_goal", 8)
            DateUtils.checkDailyReset(prefs)
            val current = prefs.getTodayCount()
            if (current < dailyGoal) {
                prefs.saveTodayCount(current + 1)
            }
            // 使用 withContext 确保在主线程更新
            withContext(Dispatchers.Main) {
                WideWidget().updateAll(context)
                NarrowWidget().updateAll(context)
            }
        } catch (e: Exception) {
            Log.e("Widget", "Increment Error", e)
        }
    }
}