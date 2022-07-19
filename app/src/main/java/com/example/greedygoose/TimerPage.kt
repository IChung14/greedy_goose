package com.example.greedygoose

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.greedygoose.databinding.TimerPageBinding
import com.example.greedygoose.timer.NotificationUtil
import com.example.greedygoose.timer.TimerService
import com.example.greedygoose.timer.TimerStateContext
import com.example.greedygoose.timer.TimerUtil
import com.example.greedygoose.timer.state.NotStartedState
import com.example.greedygoose.timer.state.PausedState
import com.example.greedygoose.timer.state.RunningState

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
                NotificationUtil.removeNotification(TimerUtil.RUNNING_NOTIF_ID)
                NotificationUtil.showTimerExpired()
                stopService(mod.serviceIntent)

//                // instantiate goose with angry flag on
//                val floatingIntent = Intent(this@TimerPage, FloatingService::class.java)
//                floatingIntent.putExtra("angry", true)
//                this@TimerPage.startService(floatingIntent)
            } else {
                NotificationUtil.updateNotification("Timer is running")
            }
        }
    }

    companion object {
        fun snoozeAlarm(context: Context) {
            mod.elapsed_time = 300000L
            mod.serviceIntent.putExtra(TimerService.TIME_EXTRA, mod.elapsed_time)
            context.startService(mod.serviceIntent)
            mod.timerStateContext.setState(mod.runningState)
            mod.timerStateContext.getState()?.showUI()
        }

        fun stopAlarm() {
            mod.timerStateContext.getState()?.resetTimer()
            mod.timerStateContext.getState()?.showUI()
        }
    }
}