package com.example.greedygoose.foreground

import android.app.ActionBar
import android.content.Context
import android.os.Bundle
import android.os.ResultReceiver
import android.view.WindowManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.example.greedygoose.MainActivity
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
class FloatingGoose(context: Context) {
    var receiver: ResultReceiver? = null
    var windowModule = FloatingWindowModule(context)
    private var movementModule: MovementModule? = null
    private var moduleHelper: ((FloatingWindowModule)->MovementModule)? = null
    var job: Job? = null
    val scope = MainScope()

    fun build(): FloatingGoose {
        // creating a floating view
        windowModule.create()
        moduleHelper?.let {
            movementModule = it(windowModule)
//            movementModule!!.run()

        }
        sendAction(ACTION_ON_CREATE, Bundle())
        job = scope.launch{
            while(true) {
                movementModule!!.run()
                delay(5000)

            }}
        return this
    }

    fun observe(life: LifecycleOwner) {
        mod.observe_theme(life, windowModule.binding.gooseImg, "angry")
    }


    fun setWindowLayoutParams(
        layoutParams: WindowManager.LayoutParams = windowModule.defaultParam()
    ): FloatingGoose{
        windowModule.params = layoutParams
        return this
    }

    fun getLocation(): WindowManager.LayoutParams? {
        return this.windowModule.params
    }

    fun setMovementModule(moduleHelper: (FloatingWindowModule)->MovementModule): FloatingGoose{
        this.moduleHelper = moduleHelper
        return this
    }

    private fun sendAction(action: Int, bundle: Bundle) {
        if (receiver != null) receiver!!.send(action, bundle)
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