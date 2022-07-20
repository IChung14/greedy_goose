package com.example.greedygoose.timer.state

import com.example.greedygoose.databinding.TimerPageBinding
import com.example.greedygoose.timer.NotificationUtil
import com.example.greedygoose.timer.TimerService

abstract class TimerState(val context: TimerService) {
    abstract fun showUI(binding: TimerPageBinding)
    abstract fun nextAction(): TimerState

    fun resetTimer(): TimerState {
        NotificationUtil.removeNotification(context, TimerService.EXPIRED_NOTIF_ID)
        NotificationUtil.removeNotification(context, TimerService.RUNNING_NOTIF_ID)
        context.pauseTimer()
        context.elapsedTime.value = context.setTime

        return NotStartedState(context)
    }
}