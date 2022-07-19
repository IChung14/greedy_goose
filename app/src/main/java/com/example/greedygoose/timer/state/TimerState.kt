package com.example.greedygoose.timer.state

interface TimerState {
    fun showUI()
    fun resetTimer()
    fun nextAction()
}