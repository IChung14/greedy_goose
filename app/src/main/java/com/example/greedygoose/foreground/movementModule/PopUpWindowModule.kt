package com.example.greedygoose.foreground.movementModule

import android.R
import android.animation.*
import android.graphics.Point
import android.view.Display
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import com.example.greedygoose.foreground.FloatingComponent
import com.example.greedygoose.foreground.ui.FloatingWindowModule
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


class PopUpWindowModule(
    private var params: WindowManager.LayoutParams?,
    private val rootContainer: View?,
    private var windowManager: WindowManager?,
    private var baseView: View?
) : MovementModule {
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

    override fun run() {}

    override fun start_action(binding: FloatingWindowModule?, round: Boolean) {
        is_dragged = true
        val pvhX =
            if(!round) PropertyValuesHolder.ofInt("x", -1080, -150)
            else PropertyValuesHolder.ofInt("x", -150, 1080)
        var pvhY = PropertyValuesHolder.ofInt("y", params!!.y, params!!.y)

        val movement = ValueAnimator.ofPropertyValuesHolder(pvhX, pvhY)

        movement.addUpdateListener { valueAnimator ->
            val layoutParams = rootContainer!!.getLayoutParams() as WindowManager.LayoutParams
            layoutParams.x = (valueAnimator.getAnimatedValue("x") as Int)!!
            layoutParams.y = (valueAnimator.getAnimatedValue("y") as Int)!!
            windowManager!!.updateViewLayout(rootContainer, layoutParams)
        }

        movement.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                is_dragged = false
                if(!round){
                    MainScope().launch {
                        delay(3500)
                        start_action(binding, true)
                    }
                }
            }
        })
        movement.duration = 2150
        movement.start()
    }
}