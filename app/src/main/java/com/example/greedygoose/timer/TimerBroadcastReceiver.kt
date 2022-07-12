package com.example.greedygoose.timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

import com.example.greedygoose.mod

class TimerBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            TimerContants.ACTION_SNOOZE -> {
                NotificationUtil.removeNotification(1)
                TimerPage.snoozeAlarm(context)
            }
            TimerConstants.ACTION_STOP -> {
                NotificationUtil.removeNotification(1)
                TimerPage.stopAlarm(context)
            }
        }
    }
}