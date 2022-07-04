package com.example.greedygoose.foreground.movementModule

import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.example.greedygoose.foreground.FloatingGoose

class DragtoEatModule (
    private var params: WindowManager.LayoutParams?,
    private val rootContainer: View?,
    private var windowManager: WindowManager?,
    private var baseView: View?,
    private var floatingGoose: FloatingGoose
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
        if (rootContainer != null) {
            rootContainer.performClick()
        }
        drag()
    }

    fun drag(){
        rootContainer?.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        //remember the initial position.
                        initialX = params!!.x
                        initialY = params!!.y

                        //get the touch location
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        //Calculate the X and Y coordinates of the view.
                        var new_x = (initialX + (event.rawX - initialTouchX)).toInt()
                        params!!.x = new_x
                        var new_y = (initialY + (event.rawY - initialTouchY)).toInt()
                        params!!.y = new_y
                        //Update the layout with new X & Y coordinate
                        windowManager!!.updateViewLayout(baseView, params)

                        // if the food overlaps the goose, remove it
                        val goose_params = floatingGoose.getLocation()
                        if (goose_params != null) {
                            if(Math.abs(goose_params.x - new_x) <= 200 &&
                                Math.abs(goose_params.y - new_y) <= 200){
                                destroy()
                            }
                        }
                        return true
                    }
                }
                return false
            }
        })
    }
}

