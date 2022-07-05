package com.example.greedygoose.foreground.movementModule

import com.example.greedygoose.foreground.ui.FloatingWindowModule

interface MovementModule {
    fun run()
    fun start_action(binding: FloatingWindowModule? = null)
    fun destroy()
}