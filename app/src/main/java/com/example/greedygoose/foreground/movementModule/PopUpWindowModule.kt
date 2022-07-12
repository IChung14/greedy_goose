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
import java.util.*


class PopUpWindowModule (
    private var params: WindowManager.LayoutParams?,
    private val rootContainer: View?,
    private var windowManager: WindowManager?,
    private var baseView: View?,
    private var floatingGoose: FloatingComponent
    ): MovementModule {
    override var is_alive = true
    override var isDraggable = true
    override var is_dragged = false

    override fun randomWalk(binding: FloatingWindowModule?, is_meme: Boolean?, meme: FloatingWindowModule?) {
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

    override fun run() {
        pullOut()
    }

    override fun start_action(binding: FloatingWindowModule?) {
        is_dragged = true
        var pvhX = PropertyValuesHolder.ofInt("x", -1080, 1080)
        var pvhY = PropertyValuesHolder.ofInt("y", params!!.y, params!!.y)

        val movement = ValueAnimator.ofPropertyValuesHolder(pvhX, pvhY)

        movement.addUpdateListener { valueAnimator ->
            val layoutParams = rootContainer!!.getLayoutParams() as WindowManager.LayoutParams
            layoutParams.x = (valueAnimator.getAnimatedValue("x") as Int)!!
            layoutParams.y = (valueAnimator.getAnimatedValue("y") as Int)!!
            windowManager!!.updateViewLayout(rootContainer, layoutParams)
        }

//        windowManager!!.animate().translationX(0);
        movement.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                is_dragged = false
            }
        })
        movement.duration = 4000
        movement.start()
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
                if (!is_dragged) {
                    destroy()
                }
                return false
            }
        })
    }
}

