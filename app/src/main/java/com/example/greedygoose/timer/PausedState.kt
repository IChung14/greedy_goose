package com.example.greedygoose.timer

import android.content.Context
import android.content.Intent
import android.view.View
import com.example.greedygoose.databinding.TimerPageBinding
import com.example.greedygoose.mod

class PausedState (): TimerState {
    override fun showUI() {
        NotificationUtil.updateRunningNotification("Timer is paused")

        mod.get_binding().startBtn.text = "RESUME"

        mod.get_binding().timerText.text = TimerUtil.getTimeString()

        mod.get_binding().userInputHrs.visibility = View.INVISIBLE
        mod.get_binding().userInputMins.visibility = View.INVISIBLE
        mod.get_binding().userInputSecs.visibility = View.INVISIBLE
        mod.get_binding().timerText.visibility = View.VISIBLE
    }

    override fun resetTimer() {
        NotificationUtil.removeNotification(TimerUtil.EXPIRED_NOTIF_ID)
        NotificationUtil.removeNotification(TimerUtil.RUNNING_NOTIF_ID)
        mod.get_timer_page_context().stopService(mod.get_service_intent())
        mod.set_elapsed_time(mod.get_set_time())

        mod.get_timer_state_context().setState(mod.get_not_started_state())
    }

    // Resume timer
    override fun nextAction() {
        mod.get_service_intent().putExtra(TimerService.TIME_EXTRA, mod.get_elapsed_time())
        mod.get_timer_page_context().startService(mod.get_service_intent())
        mod.get_timer_state_context().setState(mod.get_running_state())
    }
}
