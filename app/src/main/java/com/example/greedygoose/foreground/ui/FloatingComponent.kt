package com.example.greedygoose.foreground.ui

import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.view.WindowManager
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.LiveData
import com.example.greedygoose.data.GooseState
import com.example.greedygoose.foreground.FloatingViewModel
import com.example.greedygoose.foreground.movementModule.MovementModule


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

    fun getLocation(): WindowManager.LayoutParams {
        return this.windowModule.params
    }

    protected fun sendAction(action: Int, bundle: Bundle) {
        receiver?.send(action, bundle)
    }

    fun destroy() {
        movementModule?.destroy()
        isAlive = false
        sendAction(ACTION_ON_CLOSE, Bundle())
        windowModule.destroy()
    }

    companion object {
        const val ACTION_ON_CREATE = 0x0002
        const val ACTION_ON_CLOSE = 0x00001
    }
}

class FloatingGoose(
    windowModule: FloatingWindowModule,
    movementModule: MovementModule
): FloatingComponent(windowModule, movementModule) {

    init { movementModule.startAction() }
}

class FloatingEgg(
    private val context: LifecycleService,
    windowModule: FloatingWindowModule,
    movementModule: MovementModule,
    receiver: ResultReceiver? = null
): FloatingComponent(windowModule, movementModule, receiver) {

    fun expireEgg(gooseState: LiveData<GooseState>) {
        gooseState.observe(context){
            if(it == GooseState.KILL_GOOSE) destroy()
        }
        Handler().postDelayed( {
            if (movementModule?.isAlive == true) {
                destroy()
                movementModule.destroy()
            }
        }, 15000)
    }
}

class FloatingFood(
    private val context: LifecycleService,
    private val viewModel: FloatingViewModel,
    windowModule: FloatingWindowModule,
    movementModule: MovementModule,
    receiver: ResultReceiver? = null
): FloatingComponent(windowModule, movementModule, receiver) {

    fun expireFood(gooseState: LiveData<GooseState>) {
        gooseState.observe(context){
            if(it == GooseState.KILL_GOOSE) destroy()
        }

        Handler().postDelayed({
            if (movementModule?.isAlive == true) {
                viewModel.decrementEggCount(if (viewModel.eggCount.value >= 5) 5 else viewModel.eggCount.value)
                destroy()
            }
        }, 15000)
    }
}

class FloatingWindow(
    windowModule: FloatingWindowModule,
    movementModule: MovementModule
): FloatingComponent(windowModule, movementModule) {}

class FloatingPrints(
    private val context: LifecycleService,
    windowModule: FloatingWindowModule,
    movementModule: MovementModule? = null,
    receiver: ResultReceiver? = null
): FloatingComponent(windowModule, movementModule, receiver) {

    fun expirePrints(gooseState: LiveData<GooseState>) {
        gooseState.observe(context){
            if(it == GooseState.KILL_GOOSE) destroy()
        }
        Handler().postDelayed( {
            destroy()
        }, 10000)
    }
}
