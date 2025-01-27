package com.example.greedygoose.foreground.movementModule

import android.animation.*
import android.view.WindowManager
import com.example.greedygoose.data.Direction
import com.example.greedygoose.foreground.ui.FloatingWindowModule
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PopUpWindowModule(
    windowModule: FloatingWindowModule
) : MovementModule(windowModule) {

    var movement: ValueAnimator? = null

    override fun run() {}

    override fun startAction(round: Boolean, dir: Direction) {
        isDragged = true
        var pvhX =
            if (!round) PropertyValuesHolder.ofInt("x", -1080, -150)
            else PropertyValuesHolder.ofInt("x", -150, 1080)
        if (dir == Direction.RIGHT) {
            pvhX =
                if (!round) PropertyValuesHolder.ofInt("x", 1080, 150)
                else PropertyValuesHolder.ofInt("x", 150, -1080)
        }
        var pvhY = PropertyValuesHolder.ofInt("y", params.y, params.y)

        movement = ValueAnimator.ofPropertyValuesHolder(pvhX, pvhY)

        movement?.addUpdateListener { valueAnimator ->
            val layoutParams = rootContainer.getLayoutParams() as WindowManager.LayoutParams
            layoutParams.x = (valueAnimator.getAnimatedValue("x") as Int)
            layoutParams.y = (valueAnimator.getAnimatedValue("y") as Int)
            try {
                windowManager.updateViewLayout(rootContainer, layoutParams)
            } catch (e: IllegalArgumentException) {
                movement?.removeAllUpdateListeners()
                println("im tired")
            }
        }

        movement?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                isDragged = false
                if (!round) {
                    MainScope().launch {
                        delay(3500)
                        startAction(true, dir)
                    }
                }
            }
        })
        movement?.duration = 2150
        movement?.start()
    }

    override fun destroy() {
        movement?.removeAllUpdateListeners()
        movement?.cancel()
        super.destroy()
    }
}