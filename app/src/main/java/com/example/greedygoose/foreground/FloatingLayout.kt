package com.example.greedygoose.foreground

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.annotation.LayoutRes

class FloatingLayout(private val context: Context, @param:LayoutRes private val layoutRes: Int) {
    private var floatingListener: FloatingListener? = null
    var isShow = false
        private set
    private var intent: Intent? = null
        get() {
            if (field == null) field = Intent(context, FloatingService::class.java)
            return field
        }

    fun setFloatingListener(floatingListener: FloatingListener?) {
        this.floatingListener = floatingListener
    }

    fun create() {
        isShow = true
        val intent = intent!!
        intent.putExtra(FloatingService.EXTRA_LAYOUT_RESOURCE, layoutRes)
        if (floatingListener != null) intent.putExtra(
            FloatingService.EXTRA_RECEIVER, FloatingResult(
                Handler(Looper.getMainLooper()), floatingListener
            )
        )
        context.startService(intent)
    }

    fun destroy() {
        isShow = false
        context.stopService(intent)
    }

}