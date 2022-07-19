package com.example.greedygoose.timer

import android.content.Context
import android.content.Intent
import android.view.View
import com.example.greedygoose.databinding.TimerPageBinding
import com.example.greedygoose.mod

class PausedState (context: TimerService) : TimerState(context) {

    override fun showUI(binding: TimerPageBinding) {
        NotificationUtil.updateRunningNotification(context,"Timer is paused", context.elapsedTime)

        binding.startBtn.text = "RESUME"

        binding.timerText.text = TimerUtil.getTimeString(context.getTime())

        binding.userInputHrs.visibility = View.INVISIBLE
        binding.userInputMins.visibility = View.INVISIBLE
        binding.userInputSecs.visibility = View.INVISIBLE
        binding.timerText.visibility = View.VISIBLE
    }

    // Resume timer
    override fun nextAction(): TimerState {
        val intent = Intent(context, TimerService::class.java)
        intent.putExtra(TimerService.TIME_EXTRA, context.elapsedTime)
        context.startService(intent)

        return RunningState(context)
    }
}
