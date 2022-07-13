package com.example.greedygoose.foreground.movementModule


import android.animation.*
import android.app.Service
import android.content.Context.POWER_SERVICE
import android.graphics.Point
import android.os.PowerManager
import android.util.DisplayMetrics
import android.view.*
import android.view.View.OnTouchListener
import com.example.greedygoose.foreground.FloatingComponent
import com.example.greedygoose.foreground.ui.FloatingWindowModule
import com.example.greedygoose.mod
import com.example.greedygoose.theme_map
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


class DragMovementModule(
    private var params: WindowManager.LayoutParams?,
    private val rootContainer: View?,
    private var windowManager: WindowManager?,
    private var baseView: View?,
    private var context: Service?
): MovementModule {


    private var curr_theme = mod.get_theme().toString()
    private var action = mod.get_action().toString()
    override var is_alive = true
    override var isDraggable = true
    override var is_dragged = false

    private var animator: ValueAnimator? = null
        set(value) {
            field?.cancel()
            field = value
        }

    override fun run() {}

    override fun start_action(binding: FloatingWindowModule?, round: Boolean) {
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
                        randomWalk(binding, false, false,null)
                    }
                }
                delay(7000)
            }
        }
    }

    fun walkOffScreen(window: FloatingWindowModule?) {
        is_dragged = true
        isDraggable = false

        var y = Random().nextInt(1500)-1000
        var pvhX = PropertyValuesHolder.ofInt("x", params!!.x, -1080)
        var pvhY = PropertyValuesHolder.ofInt("y", params!!.y, params!!.y)
        params!!.flags = params!!.flags or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS

        animator = ValueAnimator.ofPropertyValuesHolder(pvhX, pvhY)
        val startx = params!!.x

        // Do not allow dragging while the goose is moving
        var updates = 0
        var direction = ""

        animator?.addUpdateListener { valueAnimator ->
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
                    window?.binding?.gooseImg?.setImageResource(theme_map[curr_theme]!![action]!!)
                }
            }
        }

        animator?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                // After walking, make the goose sit sometimes
                var chance = Random().nextInt(10)
                action = if (chance > 5) {
                    if (direction == "LEFT") {
                        "SITTING_LEFT"
                    } else {
                        "SITTING_RIGHT"
                    }
                } else {
                    // If the goose is not sitting, make sure it doesn't stop on the image with only one leg
                    if (direction == "LEFT") {
                        "WALKING_LEFT"
                    } else {
                        "WALKING_RIGHT"
                    }
                }
                mod.set_action(action)
                window?.binding?.gooseImg?.setImageResource(theme_map[curr_theme]!!.get(action)!!)

                // Allow dragging again when the animation finishes
                params!!.x = -1080
                isDraggable = true
                is_dragged = false
            }
        })

        animator?.duration = 2500
        animator?.start()
    }

    fun randomWalk(window: FloatingWindowModule?, is_meme: Boolean?, round: Boolean, meme: FloatingWindowModule?) {
        // Do not allow dragging while the goose is moving
        isDraggable = false
        is_dragged = true

        val displayMetrics = DisplayMetrics()
        windowManager!!.defaultDisplay.getMetrics(displayMetrics)
        var width = displayMetrics.widthPixels

        var x = min(Random().nextInt(1500)-1000, width/2 - 270)
        x = max(x, - 270)
        var y = Random().nextInt(1500)-1000

        if (is_meme == true && !round) {
            x = 200
            y = params!!.y
        }
        else if(is_meme == true && round){
            x = 1000
            y = params!!.y
        }

        var pvhX = PropertyValuesHolder.ofInt("x", params!!.x, x)
        var pvhY = PropertyValuesHolder.ofInt("y", params!!.y, y)

        animator = ValueAnimator.ofPropertyValuesHolder(pvhX, pvhY)
        val startx = params!!.x

        // Do not allow dragging while the goose is moving
        var updates = 0
        var direction = ""

        animator?.addUpdateListener { valueAnimator ->
            curr_theme = mod.get_theme().toString()
            val layoutParams = rootContainer!!.getLayoutParams() as WindowManager.LayoutParams
            layoutParams.x = (valueAnimator.getAnimatedValue("x") as Int)!!
            layoutParams.y = (valueAnimator.getAnimatedValue("y") as Int)!!
            windowManager!!.updateViewLayout(rootContainer, layoutParams)

            // For a smoother walking animation, only change the goose img every 5 animations
            updates += 1
            if (updates % 5 == 0) {
                if ((layoutParams.x > startx) xor (is_meme == true)) {
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

        animator?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if(!round && is_meme == true){
                    MainScope().launch {
                        if (direction == "LEFT") {
                            mod.set_action("WINDOW_LEFT")
                        } else {
                            mod.set_action("WINDOW_RIGHT")
                        }
                        window!!.binding.gooseImg.setImageResource(theme_map[curr_theme]!![mod.get_action()]!!)
                        delay(3500)
                        randomWalk(window, true, true, null)
                    }
                } else {
                    // After walking, make the goose sit sometimes
                    var chance = Random().nextInt(10)
                    action = if (chance > 5) {
                        if (direction == "LEFT") {
                            "SITTING_LEFT"
                        } else {
                            "SITTING_RIGHT"
                        }
                    } else {
                        // If the goose is not sitting, make sure it doesn't stop on the image with only one leg
                        if (direction == "LEFT") {
                            "WALKING_LEFT"
                        } else {
                            "WALKING_RIGHT"
                        }
                    }
                    mod.set_action(action)
                    window!!.binding.gooseImg.setImageResource(theme_map[curr_theme]!!.get(action)!!)

                    // Allow dragging again when the animation finishes
                    isDraggable = true
                    is_dragged = false
                }
            }
        })

        animator?.duration = Random().nextInt(2000).toLong() + 2500
        if (is_meme == true) {
            animator?.duration = 2000
        }
        animator?.start()
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