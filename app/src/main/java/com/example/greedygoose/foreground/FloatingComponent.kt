package com.example.greedygoose.foreground

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.view.WindowManager
import androidx.annotation.DrawableRes
import com.example.greedygoose.R
import com.example.greedygoose.foreground.movementModule.MovementModule
import com.example.greedygoose.foreground.ui.FloatingWindowModule
import com.example.greedygoose.mod
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * FloatingGoose is Semi
 */
class FloatingComponent(context: Context, type: String) {
    var receiver: ResultReceiver? = null
    var windowModule = FloatingWindowModule(context)
    var img = type
    @DrawableRes private var imgRes: Int? = null
    private var movementModule: MovementModule? = null
    private var moduleHelper: ((FloatingWindowModule)->MovementModule)? = null

    fun build(): FloatingComponent {
        // creating a floating view
        windowModule.create()
        imgRes?.let { windowModule.binding.gooseImg.setImageResource(it) }
        
        moduleHelper?.let {
            movementModule = it(windowModule)
            movementModule!!.run()
            if (img == "GOOSE") {
                movementModule!!.start_action(windowModule)
            }
        }
        sendAction(ACTION_ON_CREATE, Bundle())
        return this
    }

    fun setImageResource(@DrawableRes resId: Int): FloatingComponent{
        imgRes = resId
        return this
    }

    fun setWindowLayoutParams(params: WindowManager.LayoutParams): FloatingComponent {
        windowModule.params = params
        return this
    }

    fun setWindowLayoutParams(x: Int, y: Int): FloatingComponent {
        val layoutParams: WindowManager.LayoutParams = windowModule.defaultParam()
        layoutParams.x = x
        layoutParams.y = y
        windowModule.params = layoutParams
        return this
    }

    fun getLocation(): WindowManager.LayoutParams? {
        return this.windowModule.params
    }

    fun setMovementModule(moduleHelper: (FloatingWindowModule)->MovementModule): FloatingComponent{
        this.moduleHelper = moduleHelper
        return this
    }

    private fun sendAction(action: Int, bundle: Bundle) {
        if (receiver != null) receiver!!.send(action, bundle)
    }

    fun delete() {
        Handler().postDelayed(Runnable {
            //anything you want to start after 3s
            destroy()
            if (img == "FOOD") {
                val num_eggs = mod.get_egg_count()
                if (num_eggs != null) {
                    if (num_eggs >= 5) {
                        mod.decrease_egg_count(5)
                    } else {
                        mod.decrease_egg_count(num_eggs)
                    }
                }
            }
        }, 20000)
    }

    fun destroy() {
        sendAction(ACTION_ON_CLOSE, Bundle())

        windowModule.destroy()
        movementModule?.destroy()
        movementModule = null
    }

    companion object {
        const val ACTION_ON_CREATE = 0x0002
        const val ACTION_ON_CLOSE = 0x00001
    }
}