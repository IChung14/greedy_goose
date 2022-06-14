package com.example.greedygoose.foreground

import android.content.Context
import android.os.Bundle
import android.os.ResultReceiver
import android.view.View

class FloatingComponent(private val layoutRes: Int, private val context: Context) {
    private var receiver: ResultReceiver? = null
    private var floatingWindowModule: FloatingWindowModule? = null
    private var floatingViewMovementModule: FloatingViewMovementModule? = null

    fun setUp() {
        val ROOT_CONTAINER_ID = viewRootId
        floatingWindowModule = FloatingWindowModule(context, layoutRes)
        floatingWindowModule?.let {
            it.create()
            val floatingView: View = it.getView()
            val rootContainer = floatingView.findViewById<View>(ROOT_CONTAINER_ID)
            floatingViewMovementModule = FloatingViewMovementModule(
                it.getParams(),
                rootContainer,
                it.windowManager,
                floatingView
            )
            floatingViewMovementModule!!.run()
        }
        sendAction(ACTION_ON_CREATE, Bundle())
    }

    fun setReceiver(receiver: ResultReceiver?) {
        this.receiver = receiver
    }

    val viewRootId: Int
        get() = context.resources.getIdentifier("root_container", "id", context.packageName)

    fun getFloatingWindowModule(): FloatingWindowModule? {
        return floatingWindowModule
    }

    private fun sendAction(action: Int, bundle: Bundle) {
        if (receiver != null) receiver!!.send(action, bundle)
    }

    fun destroy() {
        sendAction(ACTION_ON_CLOSE, Bundle())

        floatingWindowModule?.destroy()
        floatingWindowModule = null
        floatingViewMovementModule?.destroy()
        floatingViewMovementModule = null
    }

    companion object {
        const val ACTION_ON_CREATE = 0x0002
        const val ACTION_ON_CLOSE = 0x00001
    }
}