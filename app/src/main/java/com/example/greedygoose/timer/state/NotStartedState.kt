package com.example.greedygoose.timer.state

import android.text.format.DateUtils
import android.view.View
import com.example.greedygoose.mod
import com.example.greedygoose.timer.NotificationUtil
import com.example.greedygoose.timer.TimerService
import com.example.greedygoose.timer.TimerUtil

class NotStartedState() : TimerState {

    override fun showUI() {
        mod.binding.startBtn.text = "START"

        mod.set_time

        val time = TimerUtil.getTime()

        mod.binding.userInputHrs.setText(time.first)
        mod.binding.userInputMins.setText(time.second)
        mod.binding.userInputSecs.setText(time.third)

        mod.binding.userInputHrs.visibility = View.VISIBLE
        mod.binding.userInputMins.visibility = View.VISIBLE
        mod.binding.userInputSecs.visibility = View.VISIBLE
        mod.binding.timerText.visibility = View.INVISIBLE
    }

    override fun resetTimer() {
        NotificationUtil.removeNotification(TimerUtil.RUNNING_NOTIF_ID)
        mod.timerPageContext.stopService(mod.serviceIntent)
        mod.elapsed_time = mod.set_time
    }

    // Start timer
    override fun nextAction() {
        val hrs = mod.binding.userInputHrs.text.toString()
        val mins = mod.binding.userInputMins.text.toString()
        val secs = mod.binding.userInputSecs.text.toString()

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

        mod.elapsed_time = elapsedHrs + elapsedMins + elapsedSecs
        mod.set_time = elapsedHrs + elapsedMins + elapsedSecs

        if (hrs.isEmpty() && mins.isEmpty() && secs.isEmpty() || mod.elapsed_time == 0L) {
            // TODO: Add snackbar to tell user to input a valid time
        }
        else {
            mod.r_notif_manager = NotificationUtil.showTimerRunning(mod.timerPageContext)
            NotificationUtil.removeNotification(TimerUtil.EXPIRED_NOTIF_ID)

            mod.serviceIntent.putExtra(TimerService.TIME_EXTRA, mod.elapsed_time)
            mod.timerPageContext.startService(mod.serviceIntent)
        }

        mod.timerStateContext.setState(mod.runningState)
    }
}