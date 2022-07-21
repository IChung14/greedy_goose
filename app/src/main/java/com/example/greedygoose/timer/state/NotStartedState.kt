package com.example.greedygoose.timer.state

import android.view.View
import com.example.greedygoose.databinding.TimerPageBinding
import com.example.greedygoose.timer.NotificationUtil
import com.example.greedygoose.timer.StateTimer
import com.example.greedygoose.timer.TimerService

class NotStartedState(context: TimerService, stateTimer: StateTimer) : TimerState(context, stateTimer) {

    override fun showUI(binding: TimerPageBinding) {
        binding.startBtn.text = "START"

        val time = stateTimer.getTime()

        binding.userInputHrs.setText(time.first)
        binding.userInputMins.setText(time.second)
        binding.userInputSecs.setText(time.third)

        binding.userInputHrs.visibility = View.VISIBLE
        binding.userInputMins.visibility = View.VISIBLE
        binding.userInputSecs.visibility = View.VISIBLE
        binding.timerText.visibility = View.INVISIBLE
    }

    // Start timer
    // next action is Running State
    override fun nextAction(): TimerState {
        NotificationUtil.removeNotification(context, TimerService.EXPIRED_NOTIF_ID)
        NotificationUtil.showTimerRunning(context)
        stateTimer.resumeTimer()

        return RunningState(context, stateTimer)
    }

}