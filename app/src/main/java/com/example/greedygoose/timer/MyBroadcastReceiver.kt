package com.example.greedygoose.timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class MyBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(context, intent?.getStringExtra("action_msg"), Toast.LENGTH_SHORT).show()
        TimerPage::snoozeTimer
    }
}