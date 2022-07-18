package com.example.greedygoose.foreground.movementModule

import android.view.View
import android.view.WindowManager
import com.example.greedygoose.data.Direction
import com.example.greedygoose.foreground.ui.FloatingWindowModule

abstract class MovementModule(
    private val windowModule:  FloatingWindowModule
) {
    var isAlive = true
    var isDraggable = true
    var isDragged = false

    protected val baseView = windowModule.binding.root
    protected val rootContainer = windowModule.binding.rootContainer
    protected val params = windowModule.params
    protected val windowManager = windowModule.windowManager

    abstract fun run()
    abstract fun startAction(floatingWindowModule: FloatingWindowModule? = null, round: Boolean = false, dir: Direction = Direction.RIGHT)
    fun destroy() {
        try {
            windowManager.removeViewImmediate(baseView)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } finally {
            this.isAlive = false
        }
    }
}