package com.example.greedygoose.foreground
import android.view.*
import android.view.View.OnTouchListener
import com.example.greedygoose.foreground.movementModule.MovementModule
import android.view.MotionEvent
import android.view.WindowManager
import android.animation.ValueAnimator
import android.animation.PropertyValuesHolder
import android.app.Service
import android.content.Context.POWER_SERVICE
import android.os.PowerManager
import java.util.*
import com.example.greedygoose.R


//import com.sun.org.apache.bcel.internal.classfile.Utility
import android.animation.AnimatorSet
import android.renderscript.ScriptGroup

import android.widget.ImageView
import com.example.greedygoose.foreground.ui.FloatingWindowModule
import com.example.greedygoose.mod


class DragMovementModule(
    private var params: WindowManager.LayoutParams?,
    private val rootContainer: View?,
    private var windowManager: WindowManager?,
    private var baseView: View?,
    private var context: Service?,
//    private var img: ImageView
): MovementModule {

    override fun run(wind: FloatingWindowModule?) {
        val powerManager = context?.getSystemService(POWER_SERVICE) as PowerManager
        if (powerManager.isInteractive) {
            val pvhX = PropertyValuesHolder.ofInt("x", params!!.x, Random().nextInt(1500)-1000)
            val pvhY = PropertyValuesHolder.ofInt("y", params!!.y, Random().nextInt(1500)-1000)

            val movement = ValueAnimator.ofPropertyValuesHolder(pvhX, pvhY)

            // Do not allow dragging while the goose is moving
            rootContainer?.setOnTouchListener(null)
            var updates = 0
            movement.addUpdateListener { valueAnimator ->
                val layoutParams = rootContainer!!.getLayoutParams() as WindowManager.LayoutParams
                layoutParams.x = (valueAnimator.getAnimatedValue("x") as Int)!!
                layoutParams.y = (valueAnimator.getAnimatedValue("y") as Int)!!
//                mod.walk()
//                mod.toggle_theme()
//                wind!!.binding.gooseImg.setImageResource(R.drawable.math_angry_left)
//                wind!!.binding.gooseImg.invalidate()
//                wind!!.binding.gooseImg.setImageResource(R.drawable.eng_angry_left2)
//                wind!!.binding.gooseImg.invalidate()
                windowManager!!.updateViewLayout(rootContainer, layoutParams)
                updates += 1
                if (updates % 5 == 0) {
                    mod.toggle_walk()

                }
            }
            movement.duration = Random().nextInt(2000).toLong() + 2500
            movement.start()

            val walking = ValueAnimator.ofPropertyValuesHolder(pvhX, pvhY)


//            walking.addUpdateListener { valueAnimator ->
//                var x = 0
//                var i = 0
//               while (i < 2) {
//                   if (x == 0) {
//                       wind!!.binding.gooseImg.setImageResource(R.drawable.math_angry_left)
//                       wind!!.binding.gooseImg.invalidate()
//
////                       windowManager!!.updateView()
////                        println("a");
////                       mod.set_theme("SCI")
////                       val layoutParams = rootContainer!!.getLayoutParams() as WindowManager.LayoutParams
////                       layoutParams.x = (valueAnimator.getAnimatedValue("x") as Int)!!
////                       layoutParams.y = (valueAnimator.getAnimatedValue("y") as Int)!!
////                       windowManager!!.updateViewLayout(rootContainer, layoutParams)
////                       context.floatingGoose.windowModule.binding.gooseImg.setImageResource(R.drawable.egg_small)
////                       if (mod != null) {
////                       mod.set_theme("SCI")
////
////                       }
////                       baseView.goos.setImageResource(R.drawable.math_angry_left)
//                       x= 1
//                   } else {
////                       println("b")
////                       mod.set_theme("MATH")
//
//                       wind!!.binding.gooseImg.setImageResource(R.drawable.math_angry_left2)
//                       wind!!.binding.gooseImg.invalidate()
////                       val layoutParams = rootContainer!!.getLayoutParams() as WindowManager.LayoutParams
////                       windowManager!!.updateViewLayout(rootContainer, layoutParams)
////
////                       if (mod != null) {
////                           mod.set_theme("MATH")
////
////                       }
//
////                       img.setImageResource(R.drawable.math_angry_left2)
//                       x = 0
//                   }
//                   i++
//               }
//            }

//                val vaW = ValueAnimator.ofInt(finalWidth, startingWidth)
//
//                vaW.addUpdateListener {
//                    val newWidth = vaW.animatedValue as Int
//                    val lp = mImageView.getLayoutParams() as LinearLayout.LayoutParams
//                    lp.width = newWidth
//                    mImageView.setLayoutParams(lp)
//                }
//
//                val vaH = ValueAnimator.ofInt(finalHeight, startingHeight)
//
//                vaW.addUpdateListener {
//                    val newHeight = vaH.animatedValue as Int
//                    val lp = mImageView.getLayoutParams() as LinearLayout.LayoutParams
//                    lp.height = newHeight
//                    mImageView.setLayoutParams(lp)
//                }
//
//                val oa: ObjectAnimator = ObjectAnimator.ofFloat(
//                    mImageView, "X", pvhX, params!!.x
//                )
//                var `as` = AnimatorSet()
//            `as`.playTogether(movement, walking)
//                `as`.duration = Random().nextInt(2000).toLong() + 2500
//                `as`.start()


//            movement.start()

            drag()

        }

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

