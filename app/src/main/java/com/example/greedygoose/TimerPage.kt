package com.example.greedygoose

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
    private var timerPopup:PopupWindow? = null

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
        stopService(serviceIntent)
        mod.set_elapsed_time(mod.get_set_time())
        mod.set_timer_state(TimerState.NOT_STARTED)
        binding.startBtn.text = "START"
        showUserInput()
    }


    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent)
        {
            mod.set_elapsed_time(intent.getLongExtra(TimerService.TIME_EXTRA, 0L))
            updateTextUI()

            if (mod.get_elapsed_time() <= 0L) {
                showPopupWindow()
                resetTimer()
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
