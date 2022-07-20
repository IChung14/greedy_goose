package com.example.greedygoose.timer

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.MutableLiveData
import com.example.greedygoose.timer.state.NotStartedState
import com.example.greedygoose.timer.state.TimerState
import java.util.*

class TimerService : Service() {
    private val binder = TimerBinder()
    private var timer: Timer? = null

    var setTime = 0L
    var elapsedTime = MutableLiveData(0L)
    var timerState:MutableLiveData<TimerState> = MutableLiveData(NotStartedState(this))

    override fun onCreate() {
        super.onCreate()
        registerReceiver(notificationReceiver, IntentFilter(NOTIF_ACTION))
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onDestroy() {
        timer?.cancel()
        unregisterReceiver(notificationReceiver)
        super.onDestroy()
    }

    fun pauseTimer(){
        timer?.cancel()
    }
    fun resumeTimer(){
        timer = Timer()
        timer?.scheduleAtFixedRate(TimeTask(elapsedTime.value ?: 0L), 1000, 1000)
    }
    private fun progressState(){
        timerState.postValue(timerState.value?.nextAction())
    }

    fun onTimerStartPressed(elapsedHrs: Long, elapsedMins: Long, elapsedSecs: Long){
        if(timerState.value is NotStartedState){
            setTime = elapsedHrs + elapsedMins + elapsedSecs
            elapsedTime.value = elapsedHrs + elapsedMins + elapsedSecs
        }
        progressState()
    }

    // also called when alarm is stopped
    fun onTimerResetPressed(){
        timerState.value = timerState.value?.resetTimer()
    }

    fun snoozeAlarm() {
        elapsedTime.value = 300000L
        progressState()
    }

    fun getTime(): Triple<String, String, String> {
        val eTime = elapsedTime.value!!
        val hr = eTime/1000/3600
        val min = (eTime/1000 - hr*3600) / 60
        val sec = (eTime/1000) % 60

        return Triple(hr.toString(), min.toString(), sec.toString())
    }

    private val notificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent)
        {
            when (intent.getStringExtra(NOTIF_EXTRA)) {
                ACTION_SNOOZE -> {
                    NotificationUtil.removeNotification(context, 1)
                    this@TimerService.snoozeAlarm()
                }
                ACTION_STOP -> {
                    NotificationUtil.removeNotification(context, 1)
                    onTimerResetPressed()
                }
            }
        }
    }

    private inner class TimeTask(private var time: Long) : TimerTask() {
        override fun run() {
            time -= 1000
            elapsedTime.postValue(time)

            if (time <= 0L) {
                NotificationUtil.showTimerExpired(this@TimerService)
                progressState()
            } else {
                NotificationUtil.updateRunningNotification(
                    this@TimerService,
                    "Timer is running",
                    time
                )
            }
        }
    }

    inner class TimerBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): TimerService = this@TimerService
    }

    companion object {
        const val RUNNING_NOTIF_ID = 0
        const val EXPIRED_NOTIF_ID = 1

        const val NOTIF_ACTION = "notificationAction"
        const val NOTIF_EXTRA = "notificationExtra"
        const val ACTION_STOP = "stop"
        const val ACTION_SNOOZE = "snooze"

        fun getTimeString(time: Triple<String, String, String>): String {
            return String.format("%02d:%02d:%02d", time.first.toInt(), time.second.toInt(),
                time.third.toInt())
        }
    }
}