package com.example.greedygoose

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.format.DateUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import com.example.greedygoose.databinding.TimerPageBinding


class TimerPage : AppCompatActivity() {

    private lateinit var binding: TimerPageBinding
    private lateinit var serviceIntent: Intent
    private var isRunning: Boolean = false;
    private var isPaused: Boolean = false;
    private var elapsedTime = 0L
    private var setTime = 0L
    private var timerPopup:PopupWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TimerPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        if (isMyServiceRunning(TimerService::class.java)) {
            binding.userInputHrs.visibility = View.INVISIBLE
            binding.userInputMins.visibility = View.INVISIBLE
            binding.userInputSecs.visibility = View.INVISIBLE
            binding.timerText.visibility = View.VISIBLE
        }

        binding.startBtn.setOnClickListener {
            if (isPaused) {
                resumeTimer()
            }
            else if (isRunning) {
                pauseTimer()
            }
            else {
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

                elapsedTime = elapsedHrs + elapsedMins + elapsedSecs
                setTime = elapsedTime

                if (hrs.isEmpty() && mins.isEmpty() && secs.isEmpty() || elapsedTime == 0L) {
                    // TODO: Add snackbar to tell user to input a valid time
                    return@setOnClickListener
                }

                startTimer()
            }
        }

        binding.resetBtn.setOnClickListener {
            resetTimer()
        }

        serviceIntent = Intent(applicationContext, TimerService::class.java)
        registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))
    }

    private fun showPopupWindow() {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.popup_window, null)

        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        timerPopup = PopupWindow(view, width, height, true)

        timerPopup?.isOutsideTouchable = true
        timerPopup?.showAtLocation(binding.root, Gravity.CENTER, 0, 0)

        val showButton = view.findViewById<Button>(R.id.window_close)
        showButton.setOnClickListener {
            dismissPopup()
        }

    }

    private fun dismissPopup() {
        timerPopup?.let {
            if(it.isShowing){
                it.dismiss()
            }
            timerPopup = null
        }

    }

    private fun pauseTimer() {
        binding.startBtn.text = "RESUME"
        stopService(serviceIntent)
        isPaused = true
    }

    private fun resumeTimer() {
        binding.startBtn.text = "PAUSE"
        startTimer()
        isPaused = false
    }

    private fun startTimer() {
        binding.userInputHrs.visibility = View.INVISIBLE
        binding.userInputMins.visibility = View.INVISIBLE
        binding.userInputSecs.visibility = View.INVISIBLE
        binding.timerText.visibility = View.VISIBLE
        serviceIntent.putExtra(TimerService.TIME_EXTRA, elapsedTime)
        startService(serviceIntent)

        isRunning = true
        binding.startBtn.text = "PAUSE"
    }

    private fun resetTimer() {
        elapsedTime = setTime
        isPaused = false
        isRunning = false
        binding.startBtn.text = "START"
        pauseTimer()
        binding.userInputHrs.visibility = View.VISIBLE
        binding.userInputMins.visibility = View.VISIBLE
        binding.userInputSecs.visibility = View.VISIBLE
        binding.timerText.visibility = View.INVISIBLE
        updateTextUI()
    }


    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent)
        {
            elapsedTime = intent.getLongExtra(TimerService.TIME_EXTRA, 0L)
            updateTextUI()

            if (elapsedTime == 0L) {
                showPopupWindow()
                resetTimer()
            }
        }
    }

    private fun updateTextUI() {
        val hr = elapsedTime/1000/3600
        val min = (elapsedTime/1000 - hr*3600) / 60
        val sec = (elapsedTime/1000) % 60

        binding.timerText.text = String.format("%02d:%02d:%02d", hr, min, sec)
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return manager.getRunningServices(Integer.MAX_VALUE)
            .any { it.service.className == serviceClass.name }
    }
}
