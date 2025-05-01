package io.github.xmoon2022.water.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.github.xmoon2022.water.R
import kotlin.random.Random

object HydrationReminderHelper {
    private const val CHANNEL_ID = "hydration_reminder"
    private const val CHANNEL_NAME = "喝水提醒"

    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "定时提醒您及时补充水分"
        }

        val notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    @SuppressLint("MissingPermission")
    fun showHydrationReminder(context: Context) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.water)
            .setContentTitle("该喝水啦！")
            .setContentText("您已经1小时没有喝水了，记得及时补充水分哦~")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context)
            .notify(Random.nextInt(), notification)
    }
}