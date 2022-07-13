package com.example.greedygoose.foreground.movementModule

import com.example.greedygoose.foreground.FloatingComponent
import com.example.greedygoose.foreground.ui.FloatingWindowModule

interface MovementModule {
    var is_alive: Boolean
    var isDraggable: Boolean
    var is_dragged: Boolean
    fun run()
    fun start_action(binding: FloatingWindowModule? = null, round: Boolean = false, dir: String = "")
    fun destroy()
}