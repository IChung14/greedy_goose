package com.example.greedygoose.timer

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.MutableLiveData
import com.example.greedygoose.mod
import java.util.*

class TimerService : Service() {
    private val binder = TimerBinder()
    private val timer = Timer()

    var setTime = 0L
    var elapsedTime = 0L
    var timerState:MutableLiveData<TimerState> = MutableLiveData(NotStartedState(this))

    override fun onCreate() {
        super.onCreate()
        registerReceiver(notificationReceiver, IntentFilter(NOTIF_ACTION))
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val time = intent.getLongExtra(TIME_EXTRA, 0L)
        timer.scheduleAtFixedRate(TimeTask(time), 1000, 1000)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        timer.cancel()
        unregisterReceiver(notificationReceiver)
        super.onDestroy()
    }

    fun onTimerStartPressed(elapsedHrs: Long, elapsedMins: Long, elapsedSecs: Long){
        if(timerState.value is NotStartedState){
            setTime = elapsedHrs + elapsedMins + elapsedSecs
            elapsedTime = elapsedHrs + elapsedMins + elapsedSecs
        }
        timerState.value = timerState.value?.nextAction()
    }

    // also called when alarm is stopped
    fun onTimerResetPressed(){
        timerState.value = timerState.value?.resetTimer()
    }

    fun snoozeAlarm() {
        elapsedTime = 300000L
        timerState.value = RunningState(this)
    }

    fun getTime(): Triple<String, String, String> {
        val hr = elapsedTime/1000/3600
        val min = (elapsedTime/1000 - hr*3600) / 60
        val sec = (elapsedTime/1000) % 60

        return Triple(hr.toString(), min.toString(), sec.toString())
    }

    private inner class TimeTask(private var time: Long) : TimerTask() {
        override fun run() {
            val intent = Intent(TIMER_UPDATED)
            time-=1000
            intent.putExtra(TIME_EXTRA, time)
            sendBroadcast(intent)
        }
    }

    inner class TimerBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): TimerService = this@TimerService
    }



    private val notificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent)
        {
            when (intent.action) {
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

    companion object {
        const val TIMER_UPDATED = "timerUpdated"
        const val TIME_EXTRA = "timeExtra"
        const val NOTIF_ACTION = "notificationAction"
        const val ACTION_STOP = "stop"
        const val ACTION_SNOOZE = "snooze"
    }
}