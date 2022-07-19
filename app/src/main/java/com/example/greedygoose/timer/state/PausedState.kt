package com.example.greedygoose.timer.state

import android.view.View
import com.example.greedygoose.mod
import com.example.greedygoose.timer.NotificationUtil
import com.example.greedygoose.timer.TimerService
import com.example.greedygoose.timer.TimerUtil

class PausedState (): TimerState {
    override fun showUI() {
        NotificationUtil.removeNotification(TimerUtil.RUNNING_NOTIF_ID)

        mod.binding.startBtn.text = "RESUME"

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

    // Resume timer
    override fun nextAction() {
        mod.serviceIntent.putExtra(TimerService.TIME_EXTRA, mod.elapsed_time)
        mod.timerPageContext.startService(mod.serviceIntent)
        mod.timerStateContext.setState(mod.runningState)
    }
}
