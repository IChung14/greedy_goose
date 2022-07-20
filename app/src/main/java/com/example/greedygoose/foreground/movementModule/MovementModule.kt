package com.example.greedygoose.foreground.movementModule

import com.example.greedygoose.data.Direction
import com.example.greedygoose.foreground.ui.FloatingWindowModule
import kotlinx.coroutines.Job

abstract class MovementModule(
    private val windowModule: FloatingWindowModule
) {
    var isAlive = true
    var isDraggable = true
    var isDragged = false
    var isFlying = false

    protected val baseView = windowModule.binding.root
    protected val rootContainer = windowModule.binding.rootContainer
    protected val params = windowModule.params
    protected val windowManager = windowModule.windowManager

    protected var job: Job? = null

    abstract fun run()
    abstract fun startAction(round: Boolean = false, dir: Direction = Direction.RIGHT)
    open fun destroy() {
        job?.cancel()
        try {
            windowManager.removeViewImmediate(baseView)
        } catch (e: IllegalArgumentException) {
        } finally {
            this.isAlive = false
        }
    }
}