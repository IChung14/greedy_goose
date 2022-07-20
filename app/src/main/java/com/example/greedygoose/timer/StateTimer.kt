package com.example.greedygoose.timer

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.example.greedygoose.data.GooseState
import com.example.greedygoose.foreground.FloatingService
import com.example.greedygoose.timer.state.NotStartedState
import com.example.greedygoose.timer.state.TimerState
import java.util.*

class StateTimer(val context: TimerService, val onExpire:()->Unit) {
    private var timer: Timer? = null

    private var setTime = 0L
    var elapsedTime = MutableLiveData(0L)
    var timerState:MutableLiveData<TimerState> = MutableLiveData(NotStartedState(context, this))

    fun initTimer(elapsedHrs: Long, elapsedMins: Long, elapsedSecs: Long){
        if(timerState.value is NotStartedState){
            setTime = elapsedHrs + elapsedMins + elapsedSecs
            elapsedTime.value = elapsedHrs + elapsedMins + elapsedSecs
        }
    }

    fun setElapsedTime(newTime: Long = setTime){
        elapsedTime.value = newTime
    }

    fun next(){ progressState() }
    fun reset() {
        timerState.value = timerState.value?.resetTimer()
    }

    fun pauseTimer(){
        timer?.cancel()
    }
    fun resumeTimer(){
        timer = Timer()
        timer?.scheduleAtFixedRate(TimeTask(elapsedTime.value ?: 0L), 1000, 1000)
    }

    fun getTime(): Triple<String, String, String> {
        val eTime = elapsedTime.value!!
        val hr = eTime/1000/3600
        val min = (eTime/1000 - hr*3600) / 60
        val sec = (eTime/1000) % 60

        return Triple(hr.toString(), min.toString(), sec.toString())
    }
    private fun progressState(){
        timerState.postValue(timerState.value?.nextAction())
    }

    fun destroy(){
        timer?.cancel()
    }

    private inner class TimeTask(private var time: Long) : TimerTask() {
        override fun run() {
            time -= 1000
            elapsedTime.postValue(time)

            if (time <= 0L) {
                NotificationUtil.showTimerExpired(context)
                progressState()
		        onExpire()
            } else {
                NotificationUtil.updateRunningNotification(
                    context,
                    "Timer is running",
                    time
                )
            }
        }
    }
}
