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
    val stateTimer = StateTimer(this){
	//TODO: onExpire behavior
	// summon angry goose
    }

    override fun onCreate() {
        super.onCreate()
        registerReceiver(notificationReceiver, IntentFilter(NOTIF_ACTION))
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onDestroy() {
        unregisterReceiver(notificationReceiver)
        super.onDestroy()
    }

    fun onTimerStartPressed(elapsedHrs: Long, elapsedMins: Long, elapsedSecs: Long){
        stateTimer.initTimer(elapsedHrs, elapsedMins, elapsedSecs)
        stateTimer.next()
    }

    // also called when alarm is stopped
    fun onTimerResetPressed(){
        stateTimer.reset()
    }

    fun snoozeAlarm() {
        stateTimer.setElapsedTime(300000L)
        stateTimer.next()
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
