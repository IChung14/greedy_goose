package com.example.greedygoose.foreground.movementModule

import android.view.MotionEvent
import android.view.View
import android.view.WindowManager

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

    override fun run() {
        rootContainer?.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                destroy()
                return false
            }
        })
        }
    }

