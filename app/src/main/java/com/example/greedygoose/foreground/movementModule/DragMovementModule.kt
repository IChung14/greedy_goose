package com.example.greedygoose.foreground

//import android.R

import android.view.*
import android.view.View.OnTouchListener
import com.example.greedygoose.R
import com.example.greedygoose.foreground.movementModule.MovementModule
import android.view.MotionEvent

import android.view.WindowManager

import android.animation.ValueAnimator

import android.animation.PropertyValuesHolder

import android.content.Context
import android.animation.Animator

import android.animation.AnimatorListenerAdapter
import android.graphics.PixelFormat


class DragMovementModule(
    private var context: Context,
    private var params: WindowManager.LayoutParams?,
    private val rootContainer: View?,
    private var windowManager: WindowManager?,
    private var baseView: View?
): MovementModule {
//    var img: ImageView? = baseView?.findViewById(R.id.gooseImg)

    override fun move() {
        val pvhX = PropertyValuesHolder.ofInt("x", params!!.x, 300)
        val pvhY = PropertyValuesHolder.ofInt("y", params!!.y, 300)

        val translator = ValueAnimator.ofPropertyValuesHolder(pvhX, pvhY)

        translator.addUpdateListener { valueAnimator ->
            val layoutParams = rootContainer!!.getLayoutParams() as WindowManager.LayoutParams
            layoutParams.x = (valueAnimator.getAnimatedValue("x") as Int)!!
            layoutParams.y = (valueAnimator.getAnimatedValue("y") as Int)!!
            windowManager!!.updateViewLayout(rootContainer, layoutParams)
        }

        translator.duration = 1000
        translator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                var p = params
                p!!.x = 300
                p!!.y = 300
                var floatingComponent : FloatingComponent = FloatingComponent(context).setWindowLayoutParams(
                    p
                ).build()
//                floatingComponent.windowModule.binding.gooseImg.width = 50
//                floatingComponent.windowModule.binding.gooseImg.height = 50

                floatingComponent.windowModule.binding.gooseImg.setImageResource(R.drawable.egg_small)

            }
        })
        translator.start()

    }

    override fun run() {
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