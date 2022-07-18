package com.example.greedygoose.foreground.ui

import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.view.WindowManager
import androidx.annotation.DrawableRes
import androidx.lifecycle.LifecycleService
import com.example.greedygoose.foreground.FloatingViewModel
import com.example.greedygoose.foreground.movementModule.MovementModule
import com.example.greedygoose.foreground.ui.FloatingWindowModule.Companion.defaultParam


/**
 * FloatingGoose is Semi
 */
abstract class FloatingComponent(
    var windowModule: FloatingWindowModule,
    val movementModule: MovementModule? = null,
    private val receiver: ResultReceiver? = null
 ) {
    private var isAlive = true

    init {
        // creating a floating view
        movementModule?.run()
        sendAction(ACTION_ON_CREATE, Bundle())
    }

    fun getLocation(): WindowManager.LayoutParams? {
        return this.windowModule.params
    }

    protected fun sendAction(action: Int, bundle: Bundle) {
        receiver?.send(action, bundle)
    }

    fun destroy() {
        isAlive = false
        sendAction(ACTION_ON_CLOSE, Bundle())
        windowModule.destroy()
        movementModule?.destroy()
    }

    companion object {
        const val ACTION_ON_CREATE = 0x0002
        const val ACTION_ON_CLOSE = 0x00001
    }
}

class FloatingGoose(
    windowModule: FloatingWindowModule,
    movementModule: MovementModule,
    receiver: ResultReceiver? = null
): FloatingComponent(windowModule, movementModule, receiver) {

    init { movementModule.startAction(windowModule) }
}

class FloatingEgg(
    windowModule: FloatingWindowModule,
    movementModule: MovementModule,
    receiver: ResultReceiver? = null
): FloatingComponent(windowModule, movementModule, receiver) {

    fun expireEgg() {
        Handler().postDelayed( {
            if (movementModule?.isAlive == true) {
                destroy()
                movementModule.destroy()
            }
        }, 15000)
    }
}

class FloatingFood(
    context: LifecycleService,
    private val viewModel: FloatingViewModel,
    windowModule: FloatingWindowModule,
    movementModule: MovementModule,
    receiver: ResultReceiver? = null
): FloatingComponent(windowModule, movementModule, receiver) {

    private var eggCount = 0
    init { viewModel.eggCount.observe(context){ eggCount = it } }

    fun expireFood() {
        Handler().postDelayed({
            if (movementModule?.isAlive == true) {
                viewModel.decrementEggCount(if (eggCount >= 5) 5 else eggCount)
                destroy()
            }
        }, 15000)
    }
}

class FloatingWindow(
    windowModule: FloatingWindowModule,
    movementModule: MovementModule,
    receiver: ResultReceiver? = null
): FloatingComponent(windowModule, movementModule, receiver) {
}

class FloatingPrints(
    windowModule: FloatingWindowModule,
    movementModule: MovementModule? = null,
    receiver: ResultReceiver? = null
): FloatingComponent(windowModule, movementModule, receiver) {

    fun expirePrints() {
        Handler().postDelayed( {
            destroy()
        }, 10000)
    }
}
