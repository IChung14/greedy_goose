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
                TimerPage.snoozeAlarm(context)
                Toast.makeText(context, mod.get_set_time().toString(), Toast.LENGTH_LONG).show()
            }
        }
    }
}