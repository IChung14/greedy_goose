package com.example.greedygoose.timer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.greedygoose.R
import com.example.greedygoose.mod

class NotificationUtil {
    companion object {
        private const val RUNNING_CHANNEL_ID = "running"
        private const val RUNNING_CHANNEL_NAME = "goose app timer running"
        private const val EXPIRED_CHANNEL_ID = "expired"
        private const val EXPIRED_CHANNEL_NAME = "goose app timer EXPIRED"
        private const val RUNNING_NOTIF_ID = 0
        private const val EXPIRED_NOTIF_ID = 1
        private var initialized = false

        fun showTimerRunning(context: Context): NotificationManager {
            val notifBuilder = getNotificationBuilder(context, RUNNING_CHANNEL_ID, false)
            notifBuilder.setContentTitle("Timer is running")
            notifBuilder.setContentText("Old")
            val notifManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notifManager.createNotificationChannel(RUNNING_CHANNEL_ID, RUNNING_CHANNEL_NAME, false)

            notifManager.notify(RUNNING_NOTIF_ID, notifBuilder.build())

            return notifManager
        }

        fun showTimerExpired(context: Context) {
            val snoozeIntent = Intent(context, TimerBroadcastReceiver::class.java)
            snoozeIntent.action = TimerContants.ACTION_SNOOZE
            var flag = PendingIntent.FLAG_UPDATE_CURRENT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                flag = PendingIntent.FLAG_IMMUTABLE or flag
            }
            val snoozePendingIntent = PendingIntent.getBroadcast(context,
                0, snoozeIntent, flag)

            val notifBuilder = getNotificationBuilder(context, EXPIRED_CHANNEL_ID, true)
            notifBuilder.setContentTitle("Time's up")
            notifBuilder.setPriority(NotificationCompat.PRIORITY_HIGH)
            notifBuilder.addAction(R.drawable.eng_flying_left, "Snooze", snoozePendingIntent)

            val notifManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notifManager.createNotificationChannel(EXPIRED_CHANNEL_ID, EXPIRED_CHANNEL_NAME, true)

            notifManager.notify(EXPIRED_NOTIF_ID, notifBuilder.build())
        }

        fun updateNotification(context: Context, content_title: String) {
            val notifBuilder = getNotificationBuilder(context, RUNNING_CHANNEL_ID, false)
            notifBuilder.setContentTitle(content_title)
            val hr = mod.get_elapsed_time()/1000/3600
            val min = (mod.get_elapsed_time()/1000 - hr*3600) / 60
            val sec = (mod.get_elapsed_time()/1000) % 60

            notifBuilder.setContentText(String.format("%02d:%02d:%02d", hr, min, sec))
            notifBuilder.setSmallIcon(R.drawable.eng_sitting_left)

            mod.get_r_notif_manager().notify(RUNNING_NOTIF_ID, notifBuilder.build());
        }

        fun removeNotifiation(notif_id: Int) {
            if(initialized){
                mod.get_r_notif_manager().cancel(notif_id)
                initialized = true
            }
        }

        private fun getNotificationBuilder(context: Context, channelId: String, playSound: Boolean)
        :NotificationCompat.Builder {
            val notifBuilder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.eng_angry_left)
                .setAutoCancel(true)
                .setDefaults(0)

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