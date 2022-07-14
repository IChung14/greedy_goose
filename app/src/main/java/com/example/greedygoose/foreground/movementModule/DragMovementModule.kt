package com.example.greedygoose.foreground.movementModule


import android.animation.*
import android.app.Service
import android.content.Context.POWER_SERVICE
import android.os.PowerManager
import android.util.DisplayMetrics
import android.view.*
import android.view.View.OnTouchListener
import com.example.greedygoose.foreground.ui.FloatingWindowModule
import com.example.greedygoose.mod
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import android.animation.Animator


import android.animation.AnimatorListenerAdapter
import com.example.greedygoose.data.Action
import com.example.greedygoose.data.Direction
import com.example.greedygoose.data.themeMap
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


class DragMovementModule(
    private var params: WindowManager.LayoutParams?,
    private val rootContainer: View?,
    private var windowManager: WindowManager?,
    private var baseView: View?,
    private var context: Service?
) : MovementModule {


    private var curr_theme = mod.getTheme()
    private var action = mod.getAction()
    override var is_alive = true
    override var isDraggable = true
    override var is_dragged = false

    private var animator: ValueAnimator? = null
        set(value) {
            field?.cancel()
            field = value
        }

    override fun run() {}

    override fun start_action(binding: FloatingWindowModule?, round: Boolean, dir: Direction) {
        // set drag listener
        drag(binding)

        // start random movements
        MainScope().launch {
            val powerManager = context?.getSystemService(POWER_SERVICE) as PowerManager
            delay(2000)
            while (true) {
                if (powerManager.isInteractive) {
                    curr_theme = mod.getTheme()
                    if (!is_dragged) {
                        randomWalk(binding, false, false, Direction.RIGHT)
                    }
                }
                delay(7000)
            }
        }
    }

    fun walkOffScreen(window: FloatingWindowModule?, dir: String) {
        is_dragged = true
        isDraggable = false

        var pvhX = PropertyValuesHolder.ofInt("x", params!!.x, -1080)
        if (dir == "RIGHT") {
            pvhX = PropertyValuesHolder.ofInt("x", params!!.x, 1080)
        }
        var pvhY = PropertyValuesHolder.ofInt("y", params!!.y, params!!.y)
        params!!.flags = params!!.flags or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS

        animator = ValueAnimator.ofPropertyValuesHolder(pvhX, pvhY)
        val startx = params!!.x

        // Do not allow dragging while the goose is moving
        var updates = 0
        var direction = Direction.RIGHT

        animator?.addUpdateListener { valueAnimator ->
            curr_theme = mod.getTheme()
            val layoutParams = rootContainer!!.getLayoutParams() as WindowManager.LayoutParams
            layoutParams.x = (valueAnimator.getAnimatedValue("x") as Int)!!
            layoutParams.y = (valueAnimator.getAnimatedValue("y") as Int)!!
            windowManager!!.updateViewLayout(rootContainer, layoutParams)
            // For a smoother walking animation, only change the goose img every 5 animations
            updates += 1
            if (updates % 5 == 0) {
                if (layoutParams.x > startx) {
                    direction = Direction.RIGHT
                    action = when (action) {
                        Action.WALKING_RIGHT -> {
                            Action.WALKING_RIGHT_MIDDLE
                        }
                        Action.WALKING_RIGHT_MIDDLE -> {
                            Action.WALKING_RIGHT2
                        }
                        else -> {
                            Action.WALKING_RIGHT
                        }
                    }
                    mod.setAction(action!!)
                    window!!.binding.gooseImg.setImageResource(themeMap[curr_theme]!![action]!!)
                } else {
                    direction = Direction.LEFT
                    action = when (action) {
                        Action.WALKING_LEFT -> {
                            Action.WALKING_LEFT_MIDDLE
                        }
                        Action.WALKING_LEFT_MIDDLE -> {
                            Action.WALKING_LEFT2
                        }
                        else -> {
                            Action.WALKING_LEFT
                        }
                    }
                    mod.setAction(action!!)
                    window?.binding?.gooseImg?.setImageResource(themeMap[curr_theme]!![action]!!)
                }
            }
        }

        animator?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                // After walking, make the goose sit sometimes
                var chance = Random().nextInt(10)
                action = if (chance > 5) {
                    if (direction == Direction.LEFT) {
                        Action.SITTING_LEFT
                    } else {
                        Action.SITTING_RIGHT
                    }
                } else {
                    // If the goose is not sitting, make sure it doesn't stop on the image with only one leg
                    if (direction == Direction.LEFT) {
                        Action.WALKING_LEFT
                    } else {
                        Action.WALKING_RIGHT
                    }
                }
                mod.setAction(action!!)
                window?.binding?.gooseImg?.setImageResource(themeMap[curr_theme]!![action]!!)

                // Allow dragging again when the animation finishes
                isDraggable = true
                is_dragged = false
            }
        })

        animator?.duration = 2500
        animator?.start()
    }

    fun randomWalk(window: FloatingWindowModule?, is_meme: Boolean?, round: Boolean, dir: Direction) {
        // Do not allow dragging while the goose is moving
        isDraggable = false
        is_dragged = true

        val displayMetrics = DisplayMetrics()
        windowManager!!.defaultDisplay.getMetrics(displayMetrics)
        var width = displayMetrics.widthPixels

        var x = min(Random().nextInt(1500) - 800, width / 2 - 270)
        x = max(x, -270)
        var y = Random().nextInt(1500) - 1000

        if (is_meme == true && !round) {
            x = 200
            y = params!!.y
            if (dir == Direction.RIGHT) {
                x = -200
            }
        } else if (is_meme == true && round) {
            x = 1000
            y = params!!.y
            if (dir == Direction.RIGHT) {
                x = -1000
            }
        }

        var pvhX = PropertyValuesHolder.ofInt("x", params!!.x, x)
        var pvhY = PropertyValuesHolder.ofInt("y", params!!.y, y)

        animator = ValueAnimator.ofPropertyValuesHolder(pvhX, pvhY)
        val startx = params!!.x

        // Do not allow dragging while the goose is moving
        var updates = 0
        var direction = Direction.RIGHT

        animator?.addUpdateListener { valueAnimator ->
            curr_theme = mod.getTheme()
            val layoutParams = rootContainer!!.getLayoutParams() as WindowManager.LayoutParams
            layoutParams.x = (valueAnimator.getAnimatedValue("x") as Int)!!
            layoutParams.y = (valueAnimator.getAnimatedValue("y") as Int)!!
            windowManager!!.updateViewLayout(rootContainer, layoutParams)

            // For a smoother walking animation, only change the goose img every 5 animations
            updates += 1
            if (updates % 5 == 0) {
                if ((layoutParams.x > startx) xor (is_meme == true)) {
                    direction = Direction.RIGHT
                    action = when (action) {
                        Action.WALKING_RIGHT -> {
                            Action.WALKING_RIGHT_MIDDLE
                        }
                        Action.WALKING_RIGHT_MIDDLE -> {
                            Action.WALKING_RIGHT2
                        }
                        else -> {
                            Action.WALKING_RIGHT
                        }
                    }
                    mod.setAction(action!!)
                    window!!.binding.gooseImg.setImageResource(themeMap[curr_theme]!![action]!!)
                } else {
                    direction = Direction.LEFT
                    action = when (action) {
                        Action.WALKING_LEFT -> {
                            Action.WALKING_LEFT_MIDDLE
                        }
                        Action.WALKING_LEFT_MIDDLE -> {
                            Action.WALKING_LEFT2
                        }
                        else -> {
                            Action.WALKING_LEFT
                        }
                    }
                    mod.setAction(action!!)
                    window!!.binding.gooseImg.setImageResource(themeMap[curr_theme]!![action]!!)
                }
            }
        }

        animator?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (!round && is_meme == true) {
                    MainScope().launch {
                        if (direction == Direction.LEFT) {
                            mod.setAction(Action.WINDOW_LEFT)
                        } else {
                            mod.setAction(Action.WINDOW_RIGHT)
                        }
                        window!!.binding.gooseImg.setImageResource(themeMap[curr_theme]!![mod.getAction()]!!)
                        delay(3500)
                        randomWalk(window, true, true, dir)
                    }
                } else {
                    // After walking, make the goose sit sometimes
                    var chance = Random().nextInt(10)
                    action = if (chance > 5) {
                        if (direction == Direction.LEFT) {
                            Action.SITTING_LEFT
                        } else {
                            Action.SITTING_RIGHT
                        }
                    } else {
                        // If the goose is not sitting, make sure it doesn't stop on the image with only one leg
                        if (direction == Direction.LEFT) {
                            Action.WALKING_LEFT
                        } else {
                            Action.WALKING_RIGHT
                        }
                    }
                    mod.setAction(action!!)
                    window!!.binding.gooseImg.setImageResource(themeMap[curr_theme]!![action]!!)

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
            private var direction = Direction.RIGHT

            override fun onTouch(v: View, event: MotionEvent): Boolean {

                // prevent touch if not draggable
                if (!isDraggable) return false
                curr_theme = mod.getTheme()

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
                        action = if (direction == Direction.LEFT) {
                            Action.SITTING_LEFT
                        } else {
                            Action.SITTING_RIGHT
                        }
                        mod.setAction(action!!)
                        window!!.binding.gooseImg.setImageResource(
                            themeMap[curr_theme]!![action]!!
                        )
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
                            if (params!!.x > prevx && (abs(params!!.x.minus(prevx)) >= 100f)) {
                                direction = Direction.RIGHT
                                if (action == Action.ANGRY_RIGHT) {
                                    action = Action.ANGRY_RIGHT2
                                } else {
                                    action = Action.ANGRY_RIGHT
                                }
                            } else if (params!!.x < prevx && (abs(params!!.x.minus(prevx)) <= 100f)) {
                                direction = Direction.LEFT
                                action = when (action) {
                                    Action.ANGRY_LEFT -> {
                                        Action.ANGRY_LEFT_MIDDLE
                                    }
                                    Action.ANGRY_LEFT_MIDDLE -> {
                                        Action.ANGRY_LEFT2
                                    }
                                    else -> {
                                        Action.ANGRY_LEFT
                                    }
                                }
                            } else {
                                // Make the goose face the right when swiping vertically
                                direction = Direction.RIGHT
                                action = when (action) {
                                    Action.ANGRY_RIGHT -> {
                                        Action.ANGRY_RIGHT_MIDDLE
                                    }
                                    Action.ANGRY_RIGHT_MIDDLE -> {
                                        Action.ANGRY_RIGHT2
                                    }
                                    else -> {
                                        Action.ANGRY_RIGHT
                                    }
                                }
                            }
                            mod.setAction(action!!)
                            window!!.binding.gooseImg.setImageResource(
                                themeMap[curr_theme]!![action]!!
                            )
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