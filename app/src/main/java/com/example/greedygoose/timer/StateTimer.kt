package com.example.greedygoose.timer

import androidx.lifecycle.MutableLiveData
import com.example.greedygoose.timer.state.NotStartedState
import com.example.greedygoose.timer.state.PausedState
import com.example.greedygoose.timer.state.RunningState
import com.example.greedygoose.timer.state.TimerState
import java.util.*

class StateTimer(val context: TimerService, val onExpire:()->Unit) {
    private var timer: Timer? = null

    private lateinit var timerHelper: TimerHelper
    private var setTime = 0L
    var elapsedTime = MutableLiveData(0L)
    var timerState:MutableLiveData<TimerState> = MutableLiveData(NotStartedState(context, this))

    fun initTimer(elapsedHrs: Long, elapsedMins: Long, elapsedSecs: Long){
        if(timerState.value is NotStartedState){
            setTime = elapsedHrs + elapsedMins + elapsedSecs
            elapsedTime.value = elapsedHrs + elapsedMins + elapsedSecs
        }
//        timer?.cancel()
//        timer = Timer()
        startBackgroundCheck()
    }

    fun setElapsedTime(newTime: Long = setTime){
        elapsedTime.value = newTime
    }

    fun next(){ progressState() }
    fun reset() {
        timerState.value = timerState.value?.resetTimer()
    }

    fun startBackgroundCheck () {
        timer?.scheduleAtFixedRate(CheckAppTask(), 0, 2000)
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
                elapsedTime.postValue(0L)
                NotificationUtil.showTimerExpired(context)
                progressState()
            } else {
                NotificationUtil.updateRunningNotification(
                    context,
                    "Timer is running",
                    time
                )
            }
        }
    }

    private inner class CheckAppTask() : TimerTask() {
        override fun run() {
            timerHelper = TimerHelper(context)
            println("BEGIN CHECKING")
            if(timerHelper.runGetAppService()){
//                println("ENTERED IF")
                if(timerState.value === NotStartedState::class.java ||
                    timerState.value === PausedState::class.java) {
                    next()
//                    timerState.postValue(timerState.value?.nextAction())
//                    println("NotStartedState PausedState")
                }
            }
            else {
//                println("ENTERED ELSE")
                if (timerState.value === RunningState::class.java) {
                    next()
//                    timerState.postValue(timerState.value?.nextAction())
//                    println("RunningState")
                }
            }

            println("FINISHED CHECKING")
        }
    }
}
