package com.example.greedygoose.timer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.example.greedygoose.R

class NotificationUtil {
    companion object {
        private const val RUNNING_CHANNEL_ID = "running"
        private const val RUNNING_CHANNEL_NAME = "goose app timer running"
        private const val EXPIRED_CHANNEL_ID = "expired"
        private const val EXPIRED_CHANNEL_NAME = "goose app timer EXPIRED"

        fun getNotifManager(context: Context) = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        fun showTimerRunning(context: Context): NotificationManager {
            val notifBuilder = getNotificationBuilder(context, RUNNING_CHANNEL_ID, false)
            notifBuilder.setContentTitle("Timer is running")
            notifBuilder.setContentText("00:00:01")

            val notifManager = getNotifManager(context)
            notifManager.createNotificationChannel(RUNNING_CHANNEL_ID, RUNNING_CHANNEL_NAME, false)
            notifManager.notify(TimerService.RUNNING_NOTIF_ID, notifBuilder.build())

            return notifManager
        }

        fun showTimerExpired(context: Context) {
            removeNotification(context, TimerService.RUNNING_NOTIF_ID)

            val snoozeIntent = Intent(TimerService.NOTIF_ACTION)
            snoozeIntent.putExtra(TimerService.NOTIF_EXTRA, TimerService.ACTION_SNOOZE)
            val stopIntent = Intent(TimerService.NOTIF_ACTION)
            stopIntent.putExtra(TimerService.NOTIF_EXTRA, TimerService.ACTION_STOP)

            val flag = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

            val snoozePendingIntent = PendingIntent.getBroadcast(context,
                0, snoozeIntent, flag)
            val stopPendingIntent = PendingIntent.getBroadcast(context,
                1, stopIntent, flag)

            val notifBuilder = getNotificationBuilder(
                context, EXPIRED_CHANNEL_ID, true)
            notifBuilder.setContentTitle("Time's up")
            notifBuilder.priority = NotificationCompat.PRIORITY_HIGH
            notifBuilder.addAction(R.drawable.eng_flying_left, "Snooze 5 min", snoozePendingIntent)
            notifBuilder.addAction(R.drawable.eng_flying_left, "Stop", stopPendingIntent)

            val notifManager = context
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notifManager.createNotificationChannel(EXPIRED_CHANNEL_ID, EXPIRED_CHANNEL_NAME, true)

            notifManager.notify(TimerService.EXPIRED_NOTIF_ID, notifBuilder.build())
        }

        fun updateRunningNotification(context: Context, content_title: String, elapsedTime: Long) {
            val notifBuilder = getNotificationBuilder(
                context, RUNNING_CHANNEL_ID, false)
            notifBuilder.setContentTitle(content_title)
            val hr = elapsedTime/1000/3600
            val min = (elapsedTime/1000 - hr*3600) / 60
            val sec = (elapsedTime/1000) % 60

            notifBuilder.setContentText(String.format("%02d:%02d:%02d", hr, min, sec))
            notifBuilder.setSmallIcon(R.drawable.eng_sitting_left)

            getNotifManager(context).notify(TimerService.RUNNING_NOTIF_ID, notifBuilder.build());
        }

        fun removeNotification(context: Context, notif_id: Int) {
            getNotifManager(context).cancel(notif_id)
        }

        private fun getNotificationBuilder(context: Context, channelId: String, playSound: Boolean)
        :NotificationCompat.Builder {
            val notifBuilder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.eng_sitting_left)
                .setAutoCancel(true)
                .setDefaults(0)
            if (playSound) {
                val notificationSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                notifBuilder.setSound(notificationSound)
            }
            return notifBuilder
        }

        private fun NotificationManager.createNotificationChannel(channelID: String, channelName: String, playSound: Boolean){
            val channelImportance = if (playSound) NotificationManager.IMPORTANCE_HIGH
            else NotificationManager.IMPORTANCE_LOW
            val notifChannel = NotificationChannel(channelID, channelName, channelImportance)
            notifChannel.enableLights(true)
            notifChannel.lightColor = Color.BLUE
            this.createNotificationChannel(notifChannel)
        }
    }
}