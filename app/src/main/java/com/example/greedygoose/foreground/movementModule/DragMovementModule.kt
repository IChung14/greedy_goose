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
import com.example.greedygoose.mod
import java.util.*
import android.animation.Animator
import com.example.greedygoose.R


import android.animation.AnimatorListenerAdapter
import com.example.greedygoose.foreground.ui.FloatingWindowModule
import kotlin.math.abs


class DragMovementModule(
    private var params: WindowManager.LayoutParams?,
    private val rootContainer: View?,
    private var windowManager: WindowManager?,
    private var baseView: View?,
    private var context: Service?
): MovementModule {

    private var isDraggable = true

    override fun run(binding: FloatingWindowModule?) {
        // set drag listener
        drag()

        // start random movements
        MainScope().launch{
            val powerManager = context?.getSystemService(POWER_SERVICE) as PowerManager
            while(true) {
                if (powerManager.isInteractive) {
                    randomWalk(binding)
                }
                delay(7000)
            }
        }
    }

    private fun randomWalk(window: FloatingWindowModule?){
        // Do not allow dragging while the goose is moving
        isDraggable = false
        var action = "WALKING_RIGHT"
        val pvhX = PropertyValuesHolder.ofInt("x", params!!.x, Random().nextInt(1500)-1000)
        val pvhY = PropertyValuesHolder.ofInt("y", params!!.y, Random().nextInt(1500)-1000)

        val movement = ValueAnimator.ofPropertyValuesHolder(pvhX, pvhY)
        val startx = params!!.x

        // Do not allow dragging while the goose is moving
        var updates = 0
        var direction = ""
        movement.addUpdateListener { valueAnimator ->
            val layoutParams = rootContainer!!.getLayoutParams() as WindowManager.LayoutParams
            layoutParams.x = (valueAnimator.getAnimatedValue("x") as Int)!!
            layoutParams.y = (valueAnimator.getAnimatedValue("y") as Int)!!
            windowManager!!.updateViewLayout(rootContainer, layoutParams)
            // For a smoother walking animation, only change the goose img every 5 animations
            updates += 1
            if (updates % 5 == 0) {
                println()
                if (layoutParams.x > startx) {
                    direction = "RIGHT"

                        if (action == "WALKING_RIGHT") {
                            action = "WALKING_RIGHT2"
                            window!!.binding.gooseImg.setImageResource(R.drawable.math_walking_right)
                        } else {
                            action = "WALKING_RIGHT"
                            window!!.binding.gooseImg.setImageResource(R.drawable.math_walking_right2)
                        }

                } else {
                    direction = "LEFT"
//                    mod.toggle_walk("LEFT", "CALM")
                    if (action == "WALKING_LEFT") {
                        action = "WALKING_LEFT"
                        window!!.binding.gooseImg.setImageResource(R.drawable.math_walking_left)
                    } else {
                        action = "WALKING_LEFT"
                        window!!.binding.gooseImg.setImageResource(R.drawable.math_walking_left)
                    }

                }
            }
        }
        movement.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                // After walking, make the goose sit sometimes
                var chance = Random().nextInt(10)
                if (chance > 5) {
                    if (direction == "LEFT") {
                        mod.set_action("SITTING_LEFT")
                    } else {
                        mod.set_action("SITTING_RIGHT")
                    }
                }
                // Allow dragging again when the animation finishes
                isDraggable = true
            }
        })
        movement.duration = Random().nextInt(2000).toLong() + 2500
        movement.start()

    }

    private fun drag() {
        rootContainer?.setOnTouchListener(object : OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f
            private var updates = 0
            private var direction = "RIGHT"

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
                    MotionEvent.ACTION_UP -> {
                        if (direction == "LEFT") {
                            mod.set_action("SITTING_LEFT")
                        } else {
                            mod.set_action("SITTING_RIGHT")
                        }
                    }
                    MotionEvent.ACTION_MOVE -> {
                        //Calculate the X and Y coordinates of the view.
                        var prevx = params!!.x
                        params!!.x = (initialX + (event.rawX - initialTouchX)).toInt()
                        params!!.y = (initialY + (event.rawY - initialTouchY)).toInt()
                        //Update the layout with new X & Y coordinate
                        windowManager!!.updateViewLayout(baseView, params)

                        // For a smoother walking animation, only change the goose img every 5 animations
                        updates += 1
                        if (updates % 5 == 0) {
                            if ( params!!.x > prevx && (abs(params!!.x.minus(prevx)) >= 100f)) {
                                direction = "RIGHT"
                                mod.toggle_walk("RIGHT", "ANGRY")
                            } else if ( params!!.x < prevx && (abs(params!!.x.minus(prevx)) <= 100f))  {
                                direction = "LEFT"
                                mod.toggle_walk("LEFT", "ANGRY")
                            } else {
                                // Make the goose face the right when swiping vertically
                                direction = "RIGHT"
                                mod.toggle_walk("RIGHT", "ANGRY")
                            }
                        }
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