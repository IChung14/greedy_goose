package com.example.greedygoose.timer

import android.view.View
import com.example.greedygoose.mod

class RunningState(): TimerState {
    override fun showUI() {
        mod.binding.startBtn.text = "PAUSE"

        mod.binding.timerText.text = TimerUtil.getTimeString()

        mod.binding.userInputHrs.visibility = View.INVISIBLE
        mod.binding.userInputMins.visibility = View.INVISIBLE
        mod.binding.userInputSecs.visibility = View.INVISIBLE
        mod.binding.timerText.visibility = View.VISIBLE
    }

    override fun resetTimer() {
        NotificationUtil.removeNotification(TimerUtil.EXPIRED_NOTIF_ID)
        NotificationUtil.removeNotification(TimerUtil.RUNNING_NOTIF_ID)
        mod.timerPageContext.stopService(mod.serviceIntent)
        mod.elapsed_time = mod.set_time

        mod.timerStateContext.setState(mod.notStartedState)
    }

    // Pause timer
    override fun nextAction() {
        NotificationUtil.updateRunningNotification("Timer is paused")
        mod.timerPageContext.stopService(mod.serviceIntent)
        mod.timerStateContext.setState(mod.pausedState)
    }
}