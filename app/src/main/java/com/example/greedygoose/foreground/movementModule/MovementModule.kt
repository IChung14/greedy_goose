package com.example.greedygoose.foreground.movementModule

import com.example.greedygoose.foreground.ui.FloatingWindowModule

interface MovementModule {
    fun run(binding: FloatingWindowModule? = null)
    fun destroy()
}