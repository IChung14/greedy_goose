package com.example.greedygoose.timer.state

import android.text.format.DateUtils
import android.view.View
import com.example.greedygoose.mod
import com.example.greedygoose.timer.NotificationUtil
import com.example.greedygoose.timer.TimerService
import com.example.greedygoose.timer.TimerUtil

class NotStartedState() : TimerState {

    override fun showUI() {
        mod.get_binding().startBtn.text = "START"

        mod.get_set_time()

        val time = TimerUtil.getTime()

        mod.get_binding().userInputHrs.setText(time.first)
        mod.get_binding().userInputMins.setText(time.second)
        mod.get_binding().userInputSecs.setText(time.third)

        mod.get_binding().userInputHrs.visibility = View.VISIBLE
        mod.get_binding().userInputMins.visibility = View.VISIBLE
        mod.get_binding().userInputSecs.visibility = View.VISIBLE
        mod.get_binding().timerText.visibility = View.INVISIBLE
    }

    override fun resetTimer() {
        NotificationUtil.removeNotification(TimerUtil.RUNNING_NOTIF_ID)
        mod.get_timer_page_context().stopService(mod.get_service_intent())
        mod.set_elapsed_time(mod.get_set_time())
    }

    // Start timer
    override fun nextAction() {
        val hrs = mod.get_binding().userInputHrs.text.toString()
        val mins = mod.get_binding().userInputMins.text.toString()
        val secs = mod.get_binding().userInputSecs.text.toString()

        var elapsedHrs = 0L
        var elapsedMins = 0L
        var elapsedSecs = 0L

        if (hrs.isNotEmpty()) {
            elapsedHrs = hrs.toLong() * DateUtils.HOUR_IN_MILLIS
        }

        if (mins.isNotEmpty()) {
            elapsedMins = mins.toLong() * DateUtils.MINUTE_IN_MILLIS
        }

        if (secs.isNotEmpty()) {
            elapsedSecs = secs.toLong() * DateUtils.SECOND_IN_MILLIS
        }

        mod.set_elapsed_time(elapsedHrs + elapsedMins + elapsedSecs)
        mod.set_set_time(elapsedHrs + elapsedMins + elapsedSecs)

        if (hrs.isEmpty() && mins.isEmpty() && secs.isEmpty() || mod.get_elapsed_time() == 0L) {
            // TODO: Add snackbar to tell user to input a valid time
        }
        else {
            mod.set_r_notif_manager(NotificationUtil.showTimerRunning(mod.get_timer_page_context()))
            NotificationUtil.removeNotification(TimerUtil.EXPIRED_NOTIF_ID)

            mod.get_service_intent().putExtra(TimerService.TIME_EXTRA, mod.get_elapsed_time())
            mod.get_timer_page_context().startService(mod.get_service_intent())
        }

        mod.get_timer_state_context().setState(mod.get_running_state())
    }
}