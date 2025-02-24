package io.github.xmoon2022.water.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.github.xmoon2022.water.utils.DateUtils
import io.github.xmoon2022.water.utils.getTodayCount
import io.github.xmoon2022.water.utils.saveTodayCount

class WaterCounterReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val dailyGoal = prefs.getInt("daily_goal", 8)
        // 每日重置检查
        DateUtils.checkDailyReset(prefs)

        val current = prefs.getTodayCount()
        when (intent.action) {
            ACTION_INCREMENT -> {
                if (current < dailyGoal) {
                    prefs.saveTodayCount(current + 1)
                }
            }
            ACTION_DECREMENT -> {
                prefs.saveTodayCount(maxOf(0, current - 1))
            }
        }

        // 更新通知
        NotificationHelper.updateNotification(context, prefs)
    }

    companion object {
        const val ACTION_INCREMENT = "ACTION_INCREMENT"
        const val ACTION_DECREMENT = "ACTION_DECREMENT"
    }
}