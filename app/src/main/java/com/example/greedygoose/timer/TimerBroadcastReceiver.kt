package com.example.greedygoose.timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.greedygoose.TimerPage

class TimerBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            TimerUtil.ACTION_SNOOZE -> {
                NotificationUtil.removeNotification(1)
                TimerPage.snoozeAlarm(context)
            }
            TimerUtil.ACTION_STOP -> {
                NotificationUtil.removeNotification(1)
                TimerPage.stopAlarm()
            }
        }
    }
}