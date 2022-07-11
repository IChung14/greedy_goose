package com.example.greedygoose.foreground.movementModule

import android.graphics.Point
import android.view.Display
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.example.greedygoose.foreground.FloatingComponent
import com.example.greedygoose.foreground.ui.FloatingWindowModule
import com.example.greedygoose.mod

class PopUpWindowModule (
    private var params: WindowManager.LayoutParams?,
    private val rootContainer: View?,
    private var windowManager: WindowManager?,
    private var baseView: View?,
    private var floatingGoose: FloatingComponent
    ): MovementModule {
    override var is_alive = true

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

    override fun run() {
        pullOut()
    }

    override fun start_action(binding: FloatingWindowModule?) {
        TODO("Not yet implemented")
    }

    private fun pullOut(){
        // only drag window onto screen if goose is at edge
        val goose_params = floatingGoose.getLocation()
        val mdisp: Display = windowManager!!.defaultDisplay
        val mdispSize = Point()
        mdisp.getSize(mdispSize)
        val max = mdispSize.x - 40

        // drag in from left to right
        if(goose_params!!.x < 40){

        }

        // drag in from right to left
        else if (goose_params!!.x > max){

        }

        // delete the meme when touched
        rootContainer?.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                destroy()
                return false
            }
        })
    }
}

