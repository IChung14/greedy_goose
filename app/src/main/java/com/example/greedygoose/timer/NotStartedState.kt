package com.example.greedygoose.timer

import android.view.View
import com.example.greedygoose.databinding.TimerPageBinding

class NotStartedState(context: TimerService) : TimerState(context) {

    override fun showUI(binding: TimerPageBinding) {
        binding.startBtn.text = "START"

        val time = context.getTime()

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
        context.resumeTimer()

        return RunningState(context)
    }
}