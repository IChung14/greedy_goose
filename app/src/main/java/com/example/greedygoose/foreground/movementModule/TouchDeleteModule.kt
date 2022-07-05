package com.example.greedygoose.foreground.movementModule

import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.example.greedygoose.*
import com.example.greedygoose.foreground.ui.FloatingWindowModule


class TouchDeleteModule (
    private var params: WindowManager.LayoutParams?,
    private val rootContainer: View?,
    private var windowManager: WindowManager?,
    private var baseView: View?
    ): MovementModule {

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
        }
    }

    override fun start_action(binding: FloatingWindowModule?) {
        TODO("Not yet implemented")
    }

    override fun run() {
        println("called egg ")

        rootContainer?.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                println("YES TOUCH")

                destroy()
                mod.increase_egg_count(1)
                return false
            }
        })
        }
}

