package com.example.greedygoose.foreground

import android.content.Context
import android.os.Bundle
import android.os.ResultReceiver
import com.example.greedygoose.foreground.movementModule.MovementModule
import com.example.greedygoose.foreground.ui.FloatingWindowModule

/**
 * FloatingComponent is Semi
 */
class FloatingComponent(context: Context) {
    var receiver: ResultReceiver? = null
    var windowModule = FloatingWindowModule(context)
    private var movementModule: MovementModule? = null

    init {
        // creating a floating view
        windowModule.create()
        sendAction(ACTION_ON_CREATE, Bundle())
    }

    fun setMovementModule(moduleHelper: (FloatingWindowModule)->MovementModule){
        movementModule = moduleHelper(windowModule)
        movementModule!!.run()
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