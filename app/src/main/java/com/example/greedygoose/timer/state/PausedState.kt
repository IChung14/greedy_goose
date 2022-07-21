package com.example.greedygoose.timer.state

import android.view.View
import com.example.greedygoose.databinding.TimerPageBinding
import com.example.greedygoose.timer.NotificationUtil
import com.example.greedygoose.timer.StateTimer
import com.example.greedygoose.timer.TimerService

class PausedState (context: TimerService, stateTimer: StateTimer) : TimerState(context, stateTimer) {

    override fun showUI(binding: TimerPageBinding) {
        binding.startBtn.text = "RESUME"

        binding.timerText.text = TimerService.getTimeString(stateTimer.getTime())

        binding.userInputHrs.visibility = View.INVISIBLE
        binding.userInputMins.visibility = View.INVISIBLE
        binding.userInputSecs.visibility = View.INVISIBLE
        binding.timerText.visibility = View.VISIBLE
    }

    // Resume timer
    override fun nextAction(): TimerState {
        stateTimer.resumeTimer()

        return RunningState(context, stateTimer)
    }
}
