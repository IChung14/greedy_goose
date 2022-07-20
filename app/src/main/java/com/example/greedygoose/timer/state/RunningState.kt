package com.example.greedygoose.timer.state

import android.view.View
import com.example.greedygoose.databinding.TimerPageBinding
import com.example.greedygoose.timer.NotificationUtil
import com.example.greedygoose.timer.StateTimer
import com.example.greedygoose.timer.TimerService

class RunningState(context: TimerService, stateTimer: StateTimer) : TimerState(context, stateTimer) {

    override fun showUI(binding: TimerPageBinding) {
        binding.startBtn.text = "PAUSE"

        binding.timerText.text = TimerService.getTimeString(stateTimer.getTime())

        binding.userInputHrs.visibility = View.INVISIBLE
        binding.userInputMins.visibility = View.INVISIBLE
        binding.userInputSecs.visibility = View.INVISIBLE
        binding.timerText.visibility = View.VISIBLE
    }

    // Pause timer
    override fun nextAction(): TimerState {
        NotificationUtil.updateRunningNotification(
            context,
            "Timer is paused",
            stateTimer.elapsedTime.value ?: 0L
        )
        stateTimer.pauseTimer()

        return PausedState(context, stateTimer)
    }
}