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
1. Problem: When user goes back to home page, the user input is empty and doesn't reflect the time they picked before
 - Change set_time to hrs, mins, secs ints and update userInput textviews.

 2. Calculation of hr,min,sec is repeated a lot, could make a TimerUtil for these types of functions. Maybe combine with constants

 3. Change timerstate to not nullable

 4. When user exits app, remove all notifs
*/

class TimerPage : AppCompatActivity() {

    companion object {
        fun snoozeAlarm(context: Context) {
            mod.set_elapsed_time(300000L)
            mod.get_service_intent().putExtra(TimerService.TIME_EXTRA, mod.get_elapsed_time())
            context.startService(mod.get_service_intent())
            mod.get_timer_state_context().setState(mod.get_running_state())
            mod.get_timer_state_context().getState()?.showUI()
        }

        fun stopAlarm() {
            mod.get_timer_state_context().getState()?.resetTimer()
            mod.get_timer_state_context().getState()?.showUI()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mod.set_binding(TimerPageBinding.inflate(layoutInflater))
        setContentView(mod.get_binding().root)

        mod.set_service_intent(Intent(applicationContext, TimerService::class.java))

        mod.set_timer_page_context(this@TimerPage)

        if (!mod.get_is_first_create()) {
            registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))

            mod.set_timer_state_context(TimerStateContext())

            mod.set_not_started_state(NotStartedState())

            mod.set_running_state(RunningState())

            mod.set_paused_state(PausedState())

            mod.get_timer_state_context().setState(mod.get_not_started_state())

            mod.set_is_first_create(true)
        }

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mod.get_timer_state_context().getState()?.showUI()

        mod.get_binding().startBtn.setOnClickListener {
            mod.get_timer_state_context().getState()?.nextAction()
            mod.get_timer_state_context().getState()?.showUI()
        }

        mod.get_binding().resetBtn.setOnClickListener {
            mod.get_timer_state_context().getState()?.resetTimer()
            mod.get_timer_state_context().getState()?.showUI()
        }

        mod.observe_entertainment(this, this)
    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent)
        {
            mod.set_elapsed_time(intent.getLongExtra(TimerService.TIME_EXTRA, 0L))
            mod.get_timer_state_context().getState()?.showUI()

            if (mod.get_elapsed_time() <= 0L) {
                NotificationUtil.removeNotification(TimerUtil.RUNNING_NOTIF_ID)
                NotificationUtil.showTimerExpired()
                stopService(mod.get_service_intent())
            } else {
                NotificationUtil.updateNotification("Timer is running")
            }
        }
    }
}
