package com.example.greedygoose.foreground.movementModule

import android.view.*
import android.view.View.OnTouchListener
import android.view.MotionEvent
import android.view.WindowManager
import android.animation.ValueAnimator
import android.animation.PropertyValuesHolder
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.app.Service
import android.content.Context.POWER_SERVICE
import android.os.PowerManager
import java.util.*


class DragMovementModule(
    private var params: WindowManager.LayoutParams?,
    private val rootContainer: View?,
    private var windowManager: WindowManager?,
    private var baseView: View?,
    private var context: Service?
): MovementModule {

    private var isDraggable = true

    override fun run() {
        // set drag listener
        drag()

        // start random movements
        MainScope().launch{
            val powerManager = context?.getSystemService(POWER_SERVICE) as PowerManager
            while(true) {
                // Do not allow dragging while the goose is moving
                if (powerManager.isInteractive) {
                    isDraggable = false
                    randomWalk()
                    isDraggable = true
                }
                delay(5000)
            }
        }
    }

    private fun randomWalk(){
        val pvhX = PropertyValuesHolder.ofInt("x", params!!.x, Random().nextInt(1500)-1000)
        val pvhY = PropertyValuesHolder.ofInt("y", params!!.y, Random().nextInt(1500)-1000)

        val movement = ValueAnimator.ofPropertyValuesHolder(pvhX, pvhY)

        // Do not allow dragging while the goose is moving
        movement.addUpdateListener { valueAnimator ->
            val layoutParams = rootContainer!!.getLayoutParams() as WindowManager.LayoutParams
            layoutParams.x = (valueAnimator.getAnimatedValue("x") as Int)!!
            layoutParams.y = (valueAnimator.getAnimatedValue("y") as Int)!!
            windowManager!!.updateViewLayout(rootContainer, layoutParams)
        }
        movement.duration = Random().nextInt(2000).toLong() + 2500
        movement.start()
    }

    private fun drag() {
        rootContainer?.setOnTouchListener(object : OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f

            override fun onTouch(v: View, event: MotionEvent): Boolean {

                // prevent touch if not draggable
                if(!isDraggable) return false

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
                        params!!.x = (initialX + (event.rawX - initialTouchX)).toInt()
                        params!!.y = (initialY + (event.rawY - initialTouchY)).toInt()
                        //Update the layout with new X & Y coordinate
                        windowManager!!.updateViewLayout(baseView, params)
                        return true
                    }
                }
                return false
            }
        })
    }

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
}