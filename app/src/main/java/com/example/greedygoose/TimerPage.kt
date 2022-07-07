package com.example.greedygoose

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.format.DateUtils
import android.view.View

import com.example.greedygoose.databinding.TimerPageBinding


class TimerPage : AppCompatActivity() {

    private lateinit var binding: TimerPageBinding
    lateinit var timer: CountDownTimer
    var isRunning: Boolean = false;
    var isPaused: Boolean = false;
    var elapsedTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TimerPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

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

                if (hrs.isEmpty() && mins.isEmpty() && secs.isEmpty() || elapsedTime == 0L) {
                    // TODO: Add snackbar to tell user to input a valid time
                    return@setOnClickListener
                }

                startTimer(elapsedTime)
            }
        }

        binding.resetBtn.setOnClickListener {
            resetTimer()
        }

    }

    private fun pauseTimer() {
        binding.startBtn.text = "RESUME"
        timer.cancel()
        isPaused = true
    }

    private fun resumeTimer() {
        binding.startBtn.text = "PAUSE"
        startTimer(elapsedTime)
        isPaused = false
    }

    private fun startTimer(time_in_seconds: Long) {
        timer = object : CountDownTimer(time_in_seconds, 1000) {
            override fun onFinish() {
            }

            override fun onTick(p0: Long) {
                elapsedTime = p0
                updateTextUI()
            }
        }
        binding.userInputHrs.visibility = View.INVISIBLE
        binding.userInputMins.visibility = View.INVISIBLE
        binding.userInputSecs.visibility = View.INVISIBLE
        binding.timerText.visibility = View.VISIBLE
        timer.start()

        isRunning = true
        binding.startBtn.text = "START"
    }

    private fun resetTimer() {
        elapsedTime = DateUtils.MINUTE_IN_MILLIS
        isPaused = false
        isRunning = false
        binding.startBtn.text = "START"
        timer.cancel()
        binding.userInputHrs.visibility = View.VISIBLE
        binding.userInputMins.visibility = View.VISIBLE
        binding.userInputSecs.visibility = View.VISIBLE
        binding.timerText.visibility = View.INVISIBLE
        updateTextUI()
    }

    private fun updateTextUI() {
        val hr = elapsedTime/1000/3600
        val min = (elapsedTime/1000 - hr*3600) / 60
        val sec = (elapsedTime/1000) % 60

        binding.timerText.text = String.format("%02d:%02d:%02d", hr, min, sec)
    }
}
