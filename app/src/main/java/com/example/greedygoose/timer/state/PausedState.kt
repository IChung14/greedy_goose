package com.example.greedygoose.timer.state

import android.view.View
import com.example.greedygoose.databinding.TimerPageBinding
import com.example.greedygoose.timer.NotificationUtil
import com.example.greedygoose.timer.TimerService

class PausedState (context: TimerService) : TimerState(context) {

    override fun showUI(binding: TimerPageBinding) {
        NotificationUtil.updateRunningNotification(
            context,
            "Timer is paused",
            context.elapsedTime.value ?: 0L
        )

        binding.startBtn.text = "RESUME"

        binding.timerText.text = TimerService.getTimeString(context.getTime())

        binding.userInputHrs.visibility = View.INVISIBLE
        binding.userInputMins.visibility = View.INVISIBLE
        binding.userInputSecs.visibility = View.INVISIBLE
        binding.timerText.visibility = View.VISIBLE
    }

    // Resume timer
    override fun nextAction(): TimerState {
        context.resumeTimer()

        return RunningState(context)
    }
}
