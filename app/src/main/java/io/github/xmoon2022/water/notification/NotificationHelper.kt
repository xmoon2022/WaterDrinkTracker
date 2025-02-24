package io.github.xmoon2022.water.notification

import android.Manifest
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import io.github.xmoon2022.water.MainActivity
import io.github.xmoon2022.water.R
import io.github.xmoon2022.water.notification.WaterCounterReceiver.Companion.ACTION_DECREMENT
import io.github.xmoon2022.water.notification.WaterCounterReceiver.Companion.ACTION_INCREMENT
import io.github.xmoon2022.water.utils.getTodayCount
import androidx.media.app.NotificationCompat.MediaStyle

object NotificationHelper {
    private const val CHANNEL_ID = "water_counter_channel"
    const val NOTIFICATION_ID = 1001
    private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1002

    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "喝水统计",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "显示和修改今日喝水杯数"
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            setShowBadge(true)
        }

        context.getSystemService(NotificationManager::class.java)
            ?.createNotificationChannel(channel)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun showNotification(context: Context, prefs: SharedPreferences) {
        val notificationManager = NotificationManagerCompat.from(context)
        try {
            if (notificationManager.areNotificationsEnabled()) {
                val notification = buildNotification(context, prefs)
                notificationManager.notify(NOTIFICATION_ID, notification)
            } else {
                if (context is Activity) {
                    ActivityCompat.requestPermissions(
                        context,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        NOTIFICATION_PERMISSION_REQUEST_CODE
                    )
                } else {
                    // 非Activity上下文处理方案
                    openAppNotificationSettings(context)
                }
            }
        } catch (e: SecurityException) {
            // 记录日志或提示用户
            Log.e("Notification", "Notification permission denied", e)
        }
    }

    fun updateNotification(context: Context, prefs: SharedPreferences) {
        val notificationManager = NotificationManagerCompat.from(context)
        try {
            if (notificationManager.areNotificationsEnabled()) {
                val notification = buildNotification(context, prefs)
                notificationManager.notify(NOTIFICATION_ID, notification)
            }
        } catch (e: SecurityException) {
            // 记录日志或提示用户
            Log.e("Notification", "Notification permission denied", e)
        }
    }

    private fun openAppNotificationSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        safeStartIntent(context, intent)
    }

    private fun safeStartIntent(context: Context, intent: Intent) {
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.e("Notification", "Settings activity not found", e)
            // 可选：显示Toast提示用户手动设置
            Toast.makeText(
                context,
                "请前往系统设置中启用通知权限",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun buildNotification(context: Context, prefs: SharedPreferences): Notification {
        // 创建点击打开应用的Intent
        val contentIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        // 创建操作按钮
        val incrementIntent = Intent(context, WaterCounterReceiver::class.java).apply {
            action = ACTION_INCREMENT
        }
        val incrementPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            incrementIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val decrementIntent = Intent(context, WaterCounterReceiver::class.java).apply {
            action = ACTION_DECREMENT
        }
        val decrementPendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            decrementIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val count = prefs.getTodayCount()

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("今日喝水：${count}杯") // 直接显示杯数在标题
            .setSmallIcon(R.drawable.water)
            .setColor(ContextCompat.getColor(context, R.color.light_blue_200))
            .setContentIntent(contentIntent)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0, 1)
            )
            .addAction(
                NotificationCompat.Action.Builder(
                    R.drawable.ic_remove,
                    "减少",
                    decrementPendingIntent
                ).build()
            )
            .addAction(
                NotificationCompat.Action.Builder(
                    R.drawable.ic_add,
                    "增加",
                    incrementPendingIntent
                ).build()
            )
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // 设置高优先级
            .setCategory(Notification.CATEGORY_STATUS) // 添加状态类别
            .build()
    }
}