package com.example.greedygoose.timer

interface TimerState {
    fun showUI(context: TimerContext)
    fun resetTimer(context: TimerContext)
}