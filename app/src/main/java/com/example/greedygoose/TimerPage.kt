package com.example.greedygoose

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.text.format.DateUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.greedygoose.databinding.TimerPageBinding
import com.example.greedygoose.timer.TimerService
import com.google.android.material.snackbar.Snackbar

class TimerPage : AppCompatActivity() {

    private lateinit var binding: TimerPageBinding

    private lateinit var timerService: TimerService
    private var timerBound: Boolean = false

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as TimerService.TimerBinder
            timerService = binder.getService()
            timerBound = true

            timerService.stateTimer.timerState.observe(this@TimerPage){
                it.showUI(binding)
            }
            timerService.stateTimer.elapsedTime.observe(this@TimerPage){
                timerService.stateTimer.timerState.value?.showUI(binding)
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            timerBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = TimerPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // when entering TimerPage, instantiate TimerService right away
        startService(Intent(applicationContext, TimerService::class.java))
        bindService(
            Intent(applicationContext, TimerService::class.java),
            connection,
            Context.BIND_AUTO_CREATE
        )

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

            if (timerService.stateTimer.elapsedTime.value!! == 0L && (hrs.isEmpty() && mins.isEmpty() && secs.isEmpty() ||
                elapsedHrs+elapsedMins+elapsedSecs == 0L)) {
                Snackbar.make(binding.root,
                    "Please input a time greater than 0 sec",
                    Snackbar.LENGTH_SHORT
                ).show()
            }else{
                timerService.onTimerStartPressed(elapsedHrs, elapsedMins, elapsedSecs)
            }

        }

        binding.resetBtn.setOnClickListener {
            timerService.onTimerResetPressed()
        }
    }

    override fun onStop() {
        timerBound = false
        super.onStop()
    }

    override fun onDestroy() {
        unbindService(connection)
        super.onDestroy()
    }

}