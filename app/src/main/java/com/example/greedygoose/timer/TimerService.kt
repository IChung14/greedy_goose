package com.example.greedygoose.timer

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import com.example.greedygoose.data.GooseState
import com.example.greedygoose.foreground.FloatingService

class TimerService : Service() {
    private val binder = TimerBinder()
    val stateTimer = StateTimer(this){

        val floatingIntent = Intent(this, FloatingService::class.java)
        floatingIntent.putExtra("flags", 2)
        startService(floatingIntent)
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

        // we kill the goose when we snooze the timer
        val murder = Intent(this@TimerService,FloatingService::class.java)
        murder.putExtra("flags", GooseState.KILL_GOOSE.ordinal)
        startService(murder)

        stateTimer.reset()
    }

    fun snoozeAlarm() {
        stateTimer.setElapsedTime(300000L)
        stateTimer.next()
    }

    private val notificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent)
        {
            // we kill the goose when we snooze the timer
            val murder = Intent(this@TimerService,FloatingService::class.java)
            murder.putExtra("flags", GooseState.KILL_GOOSE.ordinal)
            startService(murder)

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
