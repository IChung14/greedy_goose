package com.example.greedygoose.foreground.movementModule

import android.view.View
import android.view.WindowManager
import com.example.greedygoose.data.Direction
import com.example.greedygoose.foreground.FloatingViewModel
import com.example.greedygoose.foreground.ui.FloatingWindowModule


class TouchDeleteModule (
    private var params: WindowManager.LayoutParams?,
    private val rootContainer: View?,
    private var windowManager: WindowManager?,
    private var baseView: View?,
    private val viewModel: FloatingViewModel
): MovementModule {
    override var isAlive = true
    override var isDraggable = true
    override var isDragged = false

    override fun destroy() {
        try {
            windowManager?.removeViewImmediate(baseView)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } finally {
            params = null
            baseView = null
            windowManager = null
            this.isAlive = false
        }
    }

    override fun startAction(floatingWindowModule: FloatingWindowModule?, round: Boolean, dir: Direction) {}

    override fun run() {
        rootContainer?.setOnClickListener{
            destroy()
            viewModel.incrementEggCount()
        }
    }
}