package com.example.greedygoose.foreground

//import android.R

import android.R.attr
import android.graphics.PixelFormat
import android.view.*
import android.view.View.OnTouchListener
import android.widget.ImageView
import com.example.greedygoose.R
import com.example.greedygoose.foreground.movementModule.MovementModule
import android.view.MotionEvent

import android.os.SystemClock
import android.view.WindowManager

import android.animation.ValueAnimator

import android.R.attr.endY

import android.R.attr.startY

import android.animation.PropertyValuesHolder

import android.R.attr.endX

import android.R.attr.startX
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator.AnimatorUpdateListener
import java.util.*


class DragMovementModule(
    private var params: WindowManager.LayoutParams?,
    private val rootContainer: View?,
    private var windowManager: WindowManager?,
    private var baseView: View?
): MovementModule {
//    var img: ImageView? = baseView?.findViewById(R.id.gooseImg)

//    override fun move() {
//        val pvhX = PropertyValuesHolder.ofInt("x", params!!.x, 300)
//        val pvhY = PropertyValuesHolder.ofInt("y", params!!.y, 300)
//
//        val translator = ValueAnimator.ofPropertyValuesHolder(pvhX, pvhY)
//
//        translator.addUpdateListener { valueAnimator ->
//            val layoutParams = rootContainer!!.getLayoutParams() as WindowManager.LayoutParams
//            layoutParams.x = (valueAnimator.getAnimatedValue("x") as Int)!!
//            layoutParams.y = (valueAnimator.getAnimatedValue("y") as Int)!!
//            windowManager!!.updateViewLayout(rootContainer, layoutParams)
//        }
//
//        translator.duration = 1000
//        translator.start()
//    }

    override fun run() {
        val pvhX = PropertyValuesHolder.ofInt("x", params!!.x, Random().nextInt(2000)-1000)
        val pvhY = PropertyValuesHolder.ofInt("y", params!!.y, Random().nextInt(2000)-1000)

        val movement = ValueAnimator.ofPropertyValuesHolder(pvhX, pvhY)

        rootContainer?.setOnTouchListener(null)

        movement.addUpdateListener { valueAnimator ->
            val layoutParams = rootContainer!!.getLayoutParams() as WindowManager.LayoutParams
            layoutParams.x = (valueAnimator.getAnimatedValue("x") as Int)!!
            layoutParams.y = (valueAnimator.getAnimatedValue("y") as Int)!!
            windowManager!!.updateViewLayout(rootContainer, layoutParams)
        }
        movement.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                // done
            }
        })
        movement.duration = Random().nextInt(2000).toLong() + 2500
        movement.start()
        drag()
    }

    fun drag() {
        rootContainer?.setOnTouchListener(object : OnTouchListener {
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