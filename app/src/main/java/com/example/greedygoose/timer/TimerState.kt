package com.example.greedygoose.timer

interface TimerState {
    fun showUI()
    fun resetTimer()
    fun nextAction()
}