package com.example.greedygoose.timer

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.text.format.DateUtils
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
    private lateinit var viewModel: TimerViewModel

    private lateinit var timerService: TimerService
    private var timerBound: Boolean = false

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as TimerService.TimerBinder
            timerService = binder.getService()
            timerBound = true

            timerService.timerState.observe(this@TimerPage){
                it.showUI(binding)
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            timerBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = TimerViewModel()

        binding = TimerPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // when entering TimerPage, instantiate TimerService right away
        bindService(
            Intent(applicationContext, TimerService::class.java),
            connection,
            Context.BIND_AUTO_CREATE
        )

        registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // maybe first call
//        timerService.timerState.value?.showUI(binding)

        binding.startBtn.setOnClickListener {
            val hrs = binding.userInputHrs.text.toString()
            val mins = binding.userInputMins.text.toString()
            val secs = binding.userInputSecs.text.toString()

            val elapsedHrs = if (hrs.isNotEmpty()) hrs.toLong() * DateUtils.HOUR_IN_MILLIS else 0L
            val elapsedMins = if (mins.isNotEmpty()) mins.toLong() * DateUtils.MINUTE_IN_MILLIS else 0L
            val elapsedSecs = if (secs.isNotEmpty()) secs.toLong() * DateUtils.SECOND_IN_MILLIS else  0L

            if (hrs.isEmpty() && mins.isEmpty() && secs.isEmpty() ||
                elapsedHrs+elapsedMins+elapsedSecs == 0L) {
                // TODO: Add snackbar to tell user to input a valid time
            }else{
                timerService.onTimerStartPressed(elapsedHrs, elapsedMins, elapsedSecs)
            }

        }

        binding.resetBtn.setOnClickListener {
            timerService.onTimerResetPressed()
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        timerBound = false
    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent)
        {
            timerService.elapsedTime = intent.getLongExtra(TimerService.TIME_EXTRA, 0L)
            timerService.timerState.value?.showUI(binding)

            if (timerService.elapsedTime <= 0L) {
                timeout()
            } else {
                NotificationUtil.updateRunningNotification(
                    context,
                    "Timer is running",
                    timerService.elapsedTime)
            }
        }
    }
    private fun timeout() {
        NotificationUtil.showTimerExpired(this)
        stopService(Intent(applicationContext, TimerService::class.java))

//                // instantiate goose with angry flag on
//                val floatingIntent = Intent(this@TimerPage, FloatingService::class.java)
//                floatingIntent.putExtra("angry", true)
//                this@TimerPage.startService(floatingIntent)
    }
}