package com.example.greedygoose.timer

interface TimerState {
    fun showUI(timerStateContext: TimerContext)
    fun resetTimer(timerStateContext: TimerContext)
}