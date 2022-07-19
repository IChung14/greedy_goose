package com.example.greedygoose.timer

import android.content.Context
import android.content.Intent
import android.view.View
import com.example.greedygoose.databinding.TimerPageBinding

class RunningState(context: TimerService) : TimerState(context) {

    override fun showUI(binding: TimerPageBinding) {
        binding.startBtn.text = "PAUSE"

        binding.timerText.text = TimerUtil.getTimeString(context.getTime())

        binding.userInputHrs.visibility = View.INVISIBLE
        binding.userInputMins.visibility = View.INVISIBLE
        binding.userInputSecs.visibility = View.INVISIBLE
        binding.timerText.visibility = View.VISIBLE
    }

    // Pause timer
    override fun nextAction(): TimerState {
        NotificationUtil.updateRunningNotification(context,"Timer is paused", context.elapsedTime)
        val intent = Intent(context, TimerService::class.java)
        context.stopService(intent)

        return PausedState(context)
    }
}