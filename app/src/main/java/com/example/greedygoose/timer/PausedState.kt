package com.example.greedygoose.timer

import android.content.Context
import android.content.Intent
import com.example.greedygoose.databinding.TimerPageBinding
import com.example.greedygoose.mod

class PausedState (var binding: TimerPageBinding, var context: Context, var intent: Intent): TimerState {
    override fun showUI(timerStateContext: TimerStateContext) {


        timerStateContext.setState(this)
    }

    override fun resetTimer(timerStateContext: TimerStateContext) {
        timerStateContext.setState(this)
    }

    fun resumeTimer(timerStateContext: TimerStateContext) {
        binding.startBtn.text = "PAUSE"
        intent.putExtra(TimerService.TIME_EXTRA, mod.get_elapsed_time())
        context.startService(intent)

        timerStateContext.setState(this)
    }
}
