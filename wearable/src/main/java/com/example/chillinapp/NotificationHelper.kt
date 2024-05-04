package com.example.chillinapp

import android.app.*
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.chillinapp.presentation.MainActivity

internal object NotificationsHelper {

    private const val NOTIFICATION_CHANNEL_ID = "general_notification_channel"

    fun createNotificationChannel(context: Context) {
        val notificationManager = context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager

        // create the notification channel
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "not_channel_test",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

    fun buildNotification(context: Context): Notification {
        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.setAction(Intent.ACTION_MAIN)
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("test")
            .setContentText("test")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setContentIntent(Intent(context, MainActivity::class.java).let {
                PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
            })
            .build()
    }
}