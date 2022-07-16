package com.example.greedygoose.timer

interface TimerState {
    fun showUI(timerStateContext: TimerStateContext)
    fun resetTimer(timerStateContext: TimerStateContext)
}