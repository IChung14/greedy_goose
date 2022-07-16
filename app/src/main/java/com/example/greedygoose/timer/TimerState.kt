package com.example.greedygoose.timer

interface TimerState {
    fun showUI(context: TimerStateContext)
    fun resetTimer(context: TimerStateContext)
}