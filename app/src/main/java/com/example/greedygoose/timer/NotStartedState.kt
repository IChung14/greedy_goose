package com.example.greedygoose.timer

import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import android.view.View
import com.example.greedygoose.databinding.TimerPageBinding
import com.example.greedygoose.timer.TimerUtil

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
        NotificationUtil.removeNotification(context, TimerUtil.EXPIRED_NOTIF_ID)

        val intent = Intent(context, TimerService::class.java)
        intent.putExtra(TimerService.TIME_EXTRA, context.elapsedTime)
        context.startService(intent)

        return RunningState(context)
    }
}