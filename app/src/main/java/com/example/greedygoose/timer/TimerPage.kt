package com.example.greedygoose.timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.format.DateUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.greedygoose.TimerState
import com.example.greedygoose.databinding.TimerPageBinding
import com.example.greedygoose.mod

/*
TODO:
Problem: When user goes back to home page, the user input is empty and doesn't reflect the time they picked before
 - Change set_time to hrs, mins, secs ints and update userInput textviews.
*/

class TimerPage : AppCompatActivity() {

    companion object {
        private lateinit var intent: Intent
        private lateinit var binding: TimerPageBinding

        fun makeIntent(context: Context, binding: TimerPageBinding): Intent {
            val serviceIntent = Intent(context, TimerService::class.java)
            this.intent = serviceIntent
            this.binding = binding
            return this.intent
        }
        fun snoozeAlarm(context: Context) {
            mod.set_set_time(300000L)
            mod.set_elapsed_time(mod.get_set_time())
            this.intent.putExtra(TimerService.TIME_EXTRA, mod.get_elapsed_time())
            context.startService(this.intent)

            mod.set_timer_state(TimerState.RUNNING)
            binding.startBtn.text = "PAUSE"

            val hr = mod.get_elapsed_time()/1000/3600
            val min = (mod.get_elapsed_time()/1000 - hr*3600) / 60
            val sec = (mod.get_elapsed_time()/1000) % 60
            binding.timerText.text = String.format("%02d:%02d:%02d", hr, min, sec)

            binding.userInputHrs.visibility = View.INVISIBLE
            binding.userInputMins.visibility = View.INVISIBLE
            binding.userInputSecs.visibility = View.INVISIBLE
            binding.timerText.visibility = View.VISIBLE
        }
        fun stopAlarm(context: Context) {
            binding.startBtn.text = "START"
            binding.userInputHrs.visibility = View.VISIBLE
            binding.userInputMins.visibility = View.VISIBLE
            binding.userInputSecs.visibility = View.VISIBLE
            binding.timerText.visibility = View.INVISIBLE
            NotificationUtil.removeNotification(0)
            context.stopService(this.intent)
            mod.set_elapsed_time(mod.get_set_time())
            mod.set_timer_state(TimerState.NOT_STARTED)
        }
    }

    private lateinit var binding: TimerPageBinding
    private lateinit var serviceIntent: Intent
    private var RUNNING_NOTIF_ID = 0
    private var EXPIRED_NOTIF_ID = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TimerPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        if (mod.get_timer_state() == TimerState.PAUSED) {
            showTimer()
            binding.startBtn.text = "RESUME"
        } else if (mod.get_timer_state() == TimerState.RUNNING) {
            showTimer()
            binding.startBtn.text = "PAUSE"
        }

        binding.startBtn.setOnClickListener {
            if (mod.get_timer_state() == TimerState.PAUSED) {
                resumeTimer()
            }
            else if (mod.get_timer_state() == TimerState.RUNNING) {
                pauseTimer()
            }
            else if (mod.get_timer_state() == TimerState.NOT_STARTED) {
                val hrs = binding.userInputHrs.text.toString()
                val mins = binding.userInputMins.text.toString()
                val secs = binding.userInputSecs.text.toString()

                // TODO: handle cases for above 60
                var elapsedHrs = 0L
                var elapsedMins = 0L
                var elapsedSecs = 0L

                if (hrs.isNotEmpty()) {
                    elapsedHrs = hrs.toLong() * DateUtils.HOUR_IN_MILLIS
                }

                if (mins.isNotEmpty()) {
                    elapsedMins = mins.toLong() * DateUtils.MINUTE_IN_MILLIS
                }

                if (secs.isNotEmpty()) {
                    elapsedSecs = secs.toLong() * DateUtils.SECOND_IN_MILLIS
                }

                mod.set_elapsed_time(elapsedHrs + elapsedMins + elapsedSecs)
                mod.set_set_time(elapsedHrs + elapsedMins + elapsedSecs)

                if (hrs.isEmpty() && mins.isEmpty() && secs.isEmpty() || mod.get_elapsed_time() == 0L) {
                    // TODO: Add snackbar to tell user to input a valid time
                    return@setOnClickListener
                }

                mod.set_r_notif_manager(NotificationUtil.showTimerRunning(this@TimerPage))
                NotificationUtil.removeNotification(EXPIRED_NOTIF_ID)
                startTimer()
            }
        }

        binding.resetBtn.setOnClickListener {
            if (mod.get_timer_state() != TimerState.NOT_STARTED) {
                NotificationUtil.removeNotification(EXPIRED_NOTIF_ID)
                resetTimer()
            }
        }

        serviceIntent = makeIntent(applicationContext, binding)
        registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))

        mod.observe_entertainment(this, this)
    }

    private fun pauseTimer() {
        binding.startBtn.text = "RESUME"
        NotificationUtil.updateNotification(this@TimerPage, "Timer is paused")
        stopService(serviceIntent)
        mod.set_timer_state(TimerState.PAUSED)
    }

    private fun resumeTimer() {
        binding.startBtn.text = "PAUSE"
        startTimer()
        mod.set_timer_state(TimerState.RUNNING)
    }

    private fun startTimer() {
        serviceIntent.putExtra(TimerService.TIME_EXTRA, mod.get_elapsed_time())
        startService(serviceIntent)

        mod.set_timer_state(TimerState.RUNNING)
        binding.startBtn.text = "PAUSE"
        showTimer()
    }

    private fun resetTimer() {
        binding.startBtn.text = "START"
        showUserInput()
        NotificationUtil.removeNotification(RUNNING_NOTIF_ID)
        stopService(serviceIntent)
        mod.set_elapsed_time(mod.get_set_time())
        mod.set_timer_state(TimerState.NOT_STARTED)
    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent)
        {
            mod.set_elapsed_time(intent.getLongExtra(TimerService.TIME_EXTRA, 0L))
            updateTextUI()

            if (mod.get_elapsed_time() <= 0L) {
                NotificationUtil.removeNotification(RUNNING_NOTIF_ID)
                NotificationUtil.showTimerExpired(this@TimerPage)
                stopService(serviceIntent)
            } else {
                NotificationUtil.updateNotification(this@TimerPage, "Timer is running")
            }
        }
    }

    private fun updateTextUI() {
        val hr = mod.get_elapsed_time()/1000/3600
        val min = (mod.get_elapsed_time()/1000 - hr*3600) / 60
        val sec = (mod.get_elapsed_time()/1000) % 60

        binding.timerText.text = String.format("%02d:%02d:%02d", hr, min, sec)
    }

    private fun showTimer() {
        updateTextUI()
        binding.userInputHrs.visibility = View.INVISIBLE
        binding.userInputMins.visibility = View.INVISIBLE
        binding.userInputSecs.visibility = View.INVISIBLE
        binding.timerText.visibility = View.VISIBLE
    }

    private fun showUserInput() {
        binding.userInputHrs.visibility = View.VISIBLE
        binding.userInputMins.visibility = View.VISIBLE
        binding.userInputSecs.visibility = View.VISIBLE
        binding.timerText.visibility = View.INVISIBLE
    }
}
