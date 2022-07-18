package com.example.greedygoose.foreground.movementModule

import com.example.greedygoose.data.Direction
import com.example.greedygoose.foreground.ui.FloatingWindowModule

interface MovementModule {
    var isAlive: Boolean
    var isDraggable: Boolean
    var isDragged: Boolean
    fun run()
    fun startAction(floatingWindowModule: FloatingWindowModule? = null, round: Boolean = false, dir: Direction = Direction.RIGHT)
    fun destroy()
}