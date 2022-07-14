package com.example.greedygoose.foreground.movementModule

import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.example.greedygoose.*
import com.example.greedygoose.data.SPLDAccessModel
import com.example.greedygoose.foreground.FloatingComponent
import com.example.greedygoose.foreground.FloatingViewModel
import com.example.greedygoose.foreground.ui.FloatingWindowModule


class TouchDeleteModule (
    private var params: WindowManager.LayoutParams?,
    private val rootContainer: View?,
    private var windowManager: WindowManager?,
    private var baseView: View?,
    private val viewModel: FloatingViewModel
): MovementModule {
    override var is_alive = true
    override var isDraggable = true
    override var is_dragged = false

    override fun destroy() {
        try {
            if (windowManager != null) if (baseView != null) windowManager!!.removeViewImmediate(
                baseView
            )
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } finally {
            params = null
            baseView = null
            windowManager = null
            this.is_alive = false
        }
    }

    override fun start_action(binding: FloatingWindowModule?, round: Boolean, dir: String) {}

    override fun run() {
        rootContainer?.setOnClickListener{
            destroy()
            viewModel.incrementEggCount()
        }
    }
}