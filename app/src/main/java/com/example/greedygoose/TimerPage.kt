package com.example.greedygoose

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnTouchListener
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import com.example.greedygoose.databinding.TimerPageBinding


class TimerPage : AppCompatActivity() {

    var milliSeconds = 60000L

    private lateinit var binding: TimerPageBinding
    lateinit var timer: CountDownTimer
    var isRunning: Boolean = false;
    var isPaused: Boolean = false;
    var elapsedTime = 0L

    private var timerPopup:PopupWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TimerPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding.startBtn.setOnClickListener {
            if (isRunning) {
                pauseTimer()
            } else {
                val time = binding.userInputTime.text.toString()
                elapsedTime = time.toLong() * milliSeconds
                startTimer(elapsedTime)
            }
        }

        binding.resetBtn.setOnClickListener {
            resetTimer()
        }

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
        if (isPaused) {
            binding.startBtn.text = "RESUME"
            startTimer(elapsedTime)
            isPaused = false
        }
        else {
            binding.startBtn.text = "PAUSE"
            timer.cancel()
            isPaused = true
        }
    }

    private fun startTimer(time_in_seconds: Long) {
        timer = object : CountDownTimer(time_in_seconds, 1000) {
            override fun onFinish() {
                showPopupWindow()
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
        isRunning = false
        timer.cancel()
        binding.userInputTime.visibility = View.VISIBLE
        binding.timerText.visibility = View.INVISIBLE
        updateTextUI()
    }

    private fun updateTextUI() {
        val minute = (elapsedTime / 1000) / 60
        val seconds = (elapsedTime / 1000) % 60

        binding.timerText.text = "$minute:$seconds"
    }
}
