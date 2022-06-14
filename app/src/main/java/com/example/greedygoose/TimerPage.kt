package com.example.greedygoose

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View

import com.example.greedygoose.databinding.TimerPageBinding


class TimerPage : Activity() {

    var milliSeconds = 60000L

    private lateinit var binding: TimerPageBinding
    lateinit var timer: CountDownTimer
    var isRunning: Boolean = false;
    var elapsedTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TimerPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startBtn.setOnClickListener {
            if (isRunning) {
                pauseTimer()
            } else {
                val time = binding.userInputTime.text.toString()
                elapsedTime = time.toLong() * 60000L
                startTimer(elapsedTime)
            }
        }

        binding.backBtn.setOnClickListener {
            startActivity(Intent(this@TimerPage, MainActivity::class.java))
        }
    }

    private fun pauseTimer() {
        binding.startBtn.text = "START"
        timer.cancel()
        isRunning = false
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
        binding.userInputTime.visibility = View.INVISIBLE
        binding.timerText.visibility = View.VISIBLE
        timer.start()

        isRunning = true
        binding.startBtn.text = "Pause"
    }

    private fun resetTimer() {
        elapsedTime = milliSeconds
        updateTextUI()
    }

    private fun updateTextUI() {
        val minute = (milliSeconds / 1000) / 60
        val seconds = (milliSeconds / 1000) % 60

        binding.timerText.text = "$minute:$seconds"
    }
}
