package com.example.greedygoose.timer

import android.content.Context
import android.content.Intent
import com.example.greedygoose.databinding.TimerPageBinding

abstract class TimerState(val context: TimerService) {
    abstract fun showUI(binding: TimerPageBinding)
    abstract fun nextAction(): TimerState

    fun resetTimer(): TimerState {
        NotificationUtil.removeNotification(context, TimerUtil.EXPIRED_NOTIF_ID)
        NotificationUtil.removeNotification(context, TimerUtil.RUNNING_NOTIF_ID)
        context.stopService(Intent(context, TimerService::class.java))
        context.elapsedTime = context.setTime

        return NotStartedState(context)
    }
}