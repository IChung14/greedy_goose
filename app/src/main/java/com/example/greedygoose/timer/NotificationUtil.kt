package com.example.greedygoose.timer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import androidx.core.app.NotificationCompat
import com.example.greedygoose.R

class NotificationUtil {
    companion object {
        private const val CHANNEL_ID_TIMER = "timer"
        private const val CHANNEL_NAME_TIMER = "goose app timer"
        private const val TIMER_ID = 0

        fun showTimerExpired(context: Context) {
            val notifBuilder = getNotificationBuilder(context, CHANNEL_ID_TIMER, true)
            notifBuilder.setContentTitle("Time's up")
            val notifManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notifManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, true)

            notifManager.notify(TIMER_ID, notifBuilder.build())
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
            val channelImportance = if (playSound) NotificationManager.IMPORTANCE_DEFAULT
            else NotificationManager.IMPORTANCE_LOW
            val notifChannel = NotificationChannel(channelID, channelName, channelImportance)
            notifChannel.enableLights(true)
            notifChannel.lightColor = Color.BLUE
            this.createNotificationChannel(notifChannel)
        }
    }
}