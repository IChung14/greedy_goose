package com.example.greedygoose.timer

import android.content.Context
import android.content.Intent
import android.view.View
import com.example.greedygoose.databinding.TimerPageBinding
import com.example.greedygoose.mod

class RunningState(var binding: TimerPageBinding, var context: Context, var intent: Intent): TimerState {
    override fun showUI(timerStateContext: TimerStateContext) {
        val hr = mod.get_elapsed_time()/1000/3600
        val min = (mod.get_elapsed_time()/1000 - hr*3600) / 60
        val sec = (mod.get_elapsed_time()/1000) % 60

        binding.timerText.text = String.format("%02d:%02d:%02d", hr, min, sec)

        binding.userInputHrs.visibility = View.INVISIBLE
        binding.userInputMins.visibility = View.INVISIBLE
        binding.userInputSecs.visibility = View.INVISIBLE
        binding.timerText.visibility = View.VISIBLE

        timerStateContext.setState(this)
    }

    override fun resetTimer(timerStateContext: TimerStateContext) {
        NotificationUtil.removeNotification(TimerConstants.RUNNING_NOTIF_ID)
        context.stopService(intent)
        mod.set_elapsed_time(mod.get_set_time())

    }

    fun pauseTimer(timerStateContext: TimerStateContext) {
        NotificationUtil.updateNotification(context, "Timer is paused")
        context.stopService(intent)

        timerStateContext.setState(this)
    }
}