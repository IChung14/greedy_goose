package com.example.greedygoose.timer

import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import android.view.View
import com.example.greedygoose.databinding.TimerPageBinding
import com.example.greedygoose.mod

class NotStartedState(var binding: TimerPageBinding, var context: Context, var intent: Intent) : TimerState {

    override fun showUI(timerContext: TimerContext) {
        binding.userInputHrs.visibility = View.VISIBLE
        binding.userInputMins.visibility = View.VISIBLE
        binding.userInputSecs.visibility = View.VISIBLE
        binding.timerText.visibility = View.INVISIBLE

        timerContext.setState(this)
    }

    override fun resetTimer(timerContext: TimerContext) {
        binding.startBtn.text = "START"
        TimerPage.showUserInput()
        NotificationUtil.removeNotification(TimerConstants.RUNNING_NOTIF_ID)
        context.stopService(intent)
        mod.set_elapsed_time(mod.get_set_time())
        mod.set_timer_state(com.example.greedygoose.TimerState.NOT_STARTED)

        timerContext.setState(this)
    }

    fun startTimer(timerContext: TimerContext) {
        intent.putExtra(TimerService.TIME_EXTRA, mod.get_elapsed_time())
        context.startService(intent)

        mod.set_timer_state(com.example.greedygoose.TimerState.RUNNING)
        binding.startBtn.text = "PAUSE"

        val hr = mod.get_elapsed_time()/1000/3600
        val min = (mod.get_elapsed_time()/1000 - hr*3600) / 60
        val sec = (mod.get_elapsed_time()/1000) % 60

        binding.timerText.text = String.format("%02d:%02d:%02d", hr, min, sec)

        binding.userInputHrs.visibility = View.INVISIBLE
        binding.userInputMins.visibility = View.INVISIBLE
        binding.userInputSecs.visibility = View.INVISIBLE
        binding.timerText.visibility = View.VISIBLE

        timerContext.setState(this)
    }
}