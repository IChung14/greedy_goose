package com.example.greedygoose.timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.greedygoose.databinding.TimerPageBinding
import com.example.greedygoose.mod

/*
TODO:
1. Problem: Set time to 0 and start it
2. Problem: Reset or start timer when 0
3. When user exits app, remove all notifs
4. When user clicks on notification timer
5. Add snackbar when invalid user input (0s or empty)
*/

class TimerPage : AppCompatActivity() {

    private lateinit var binding: TimerPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = TimerPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mod.binding = binding

        mod.serviceIntent = Intent(applicationContext, TimerService::class.java)

        mod.timerPageContext = this@TimerPage

        if (!mod.isFirstCreate) {
            registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))

            mod.timerStateContext = TimerStateContext()

            mod.notStartedState = NotStartedState()

            mod.runningState = RunningState()

            mod.pausedState = PausedState()

            mod.timerStateContext.setState(mod.notStartedState)

            mod.isFirstCreate = true
        }

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mod.timerStateContext.getState()?.showUI()

        mod.binding.startBtn.setOnClickListener {
            mod.timerStateContext.getState()?.nextAction()
            mod.timerStateContext.getState()?.showUI()
        }

        mod.binding.resetBtn.setOnClickListener {
            mod.timerStateContext.getState()?.resetTimer()
            mod.timerStateContext.getState()?.showUI()
        }
    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent)
        {
            mod.elapsed_time = intent.getLongExtra(TimerService.TIME_EXTRA, 0L)
            mod.timerStateContext.getState()?.showUI()

            if (mod.elapsed_time <= 0L) {
                timeout()
            } else {
                NotificationUtil.updateRunningNotification("Timer is running")
            }
        }
    }
    private fun timeout() {
        NotificationUtil.showTimerExpired()
        stopService(mod.serviceIntent)

//                // instantiate goose with angry flag on
//                val floatingIntent = Intent(this@TimerPage, FloatingService::class.java)
//                floatingIntent.putExtra("angry", true)
//                this@TimerPage.startService(floatingIntent)
    }

    companion object {
        fun snoozeAlarm(context: Context) {
            mod.elapsed_time = 300000L
            mod.serviceIntent.putExtra(TimerService.TIME_EXTRA, mod.elapsed_time)
            mod.timerPageContext.startService(mod.serviceIntent)
            mod.timerStateContext.setState(mod.runningState)
            mod.timerStateContext.getState()?.showUI()
        }

        fun stopAlarm() {
            mod.timerStateContext.getState()?.resetTimer()
            mod.timerStateContext.getState()?.showUI()
        }
    }
}