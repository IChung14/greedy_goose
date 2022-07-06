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
import com.example.greedygoose.theme_map
import kotlin.math.abs


class DragMovementModule(
    private var params: WindowManager.LayoutParams?,
    private val rootContainer: View?,
    private var windowManager: WindowManager?,
    private var baseView: View?,
    private var context: Service?
): MovementModule {

    private var isDraggable = true
    private var is_dragged = false
    private var curr_theme = mod.get_theme().toString()
    private var action = mod.get_action().toString()
    override var is_alive = true

    override fun run() {}

    override fun start_action(binding: FloatingWindowModule?) {
        // set drag listener
        drag(binding)

        // start random movements
        MainScope().launch{
            val powerManager = context?.getSystemService(POWER_SERVICE) as PowerManager
            delay(2000)
            while(true) {
                if (powerManager.isInteractive) {
                    curr_theme = mod.get_theme().toString()
                    if (!is_dragged) {
                        randomWalk(binding)
                    }
                }
                delay(5000)
            }
        }
    }

    private fun randomWalk(window: FloatingWindowModule?){
        // Do not allow dragging while the goose is moving
        isDraggable = false
        val pvhX = PropertyValuesHolder.ofInt("x", params!!.x, Random().nextInt(1500)-1000)
        val pvhY = PropertyValuesHolder.ofInt("y", params!!.y, Random().nextInt(1500)-1000)

        val movement = ValueAnimator.ofPropertyValuesHolder(pvhX, pvhY)
        val startx = params!!.x

        // Do not allow dragging while the goose is moving
        var updates = 0
        var direction = ""
        movement.addUpdateListener { valueAnimator ->
            curr_theme = mod.get_theme().toString()
            val layoutParams = rootContainer!!.getLayoutParams() as WindowManager.LayoutParams
            layoutParams.x = (valueAnimator.getAnimatedValue("x") as Int)!!
            layoutParams.y = (valueAnimator.getAnimatedValue("y") as Int)!!
            windowManager!!.updateViewLayout(rootContainer, layoutParams)
            // For a smoother walking animation, only change the goose img every 5 animations
            updates += 1
            if (updates % 5 == 0) {
                if (layoutParams.x > startx) {
                    direction = "RIGHT"
                    action = when (action) {
                        "WALKING_RIGHT" -> {
                            "WALKING_RIGHT_MIDDLE"
                        }
                        "WALKING_RIGHT_MIDDLE" -> {
                            "WALKING_RIGHT2"
                        }
                        else -> {
                            "WALKING_RIGHT"
                        }
                    }
                    mod.set_action(action)
                    window!!.binding.gooseImg.setImageResource(theme_map[curr_theme]!![action]!!)
                } else {
                    direction = "LEFT"
                    action = when (action) {
                        "WALKING_LEFT" -> {
                            "WALKING_LEFT_MIDDLE"
                        }
                        "WALKING_LEFT_MIDDLE" -> {
                            "WALKING_LEFT2"
                        }
                        else -> {
                            "WALKING_LEFT"
                        }
                    }
                    mod.set_action(action)
                    window!!.binding.gooseImg.setImageResource(theme_map[curr_theme]!![action]!!)
                }
            }
        }
        movement.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                // After walking, make the goose sit sometimes
                var chance = Random().nextInt(10)
                if (chance > 5) {
                    action = if (direction == "LEFT") {
                        "SITTING_LEFT"
                    } else {
                        "SITTING_RIGHT"
                    }
                } else {
                    // If the goose is not sitting, make sure it doesn't stop on the image with only one leg
                    action = if (direction == "LEFT") {
                        "WALKING_LEFT"
                    } else {
                        "WALKING_RIGHT"
                    }
                }
                mod.set_action(action)
                window!!.binding.gooseImg.setImageResource(theme_map[curr_theme]!!.get(action)!!)
                // Allow dragging again when the animation finishes
                isDraggable = true
            }
        })
        movement.duration = Random().nextInt(2000).toLong() + 2500
        movement.start()

    }

    private fun drag(window: FloatingWindowModule?) {
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
                curr_theme = mod.get_theme().toString()

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        is_dragged = true
                        //remember the initial position.
                        initialX = params!!.x
                        initialY = params!!.y

                        //get the touch location
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        is_dragged = false
                        action = if (direction == "LEFT") {
                            "SITTING_LEFT"
                        } else {
                            "SITTING_RIGHT"
                        }
                        mod.set_action(action)
                        window!!.binding.gooseImg.setImageResource(theme_map[curr_theme]!!.get(action)!!)
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
                                if (action == "ANGRY_RIGHT") {
                                    action = "ANGRY_RIGHT2"
                                } else {
                                    action = "ANGRY_RIGHT"
                                }
                            } else if ( params!!.x < prevx && (abs(params!!.x.minus(prevx)) <= 100f))  {
                                direction = "LEFT"
                                action = when (action) {
                                    "ANGRY_LEFT" -> {
                                        "ANGRY_LEFT_MIDDLE"
                                    }
                                    "ANGRY_LEFT_MIDDLE" -> {
                                        "ANGRY_LEFT2"
                                    }
                                    else -> {
                                        "ANGRY_LEFT"
                                    }
                                }
                            } else {
                                // Make the goose face the right when swiping vertically
                                direction = "RIGHT"
                                action = when (action) {
                                    "ANGRY_RIGHT" -> {
                                        "ANGRY_RIGHT_MIDDLE"
                                    }
                                    "ANGRY_RIGHT_MIDDLE" -> {
                                        "ANGRY_RIGHT2"
                                    }
                                    else -> {
                                        "ANGRY_RIGHT"
                                    }
                                }
                            }
                            mod.set_action(action)
                            window!!.binding.gooseImg.setImageResource(theme_map[curr_theme]!!.get(action)!!)
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
            this.is_alive = false
        }
    }
}