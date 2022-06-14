package com.example.greedygoose.foreground

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.annotation.LayoutRes

class FloatingWindowModule(
    private val context: Context,
    @param:LayoutRes private val layoutRes: Int
) {
    private var params: WindowManager.LayoutParams? = null
    private var view: View? = null
    var windowManager: WindowManager? = null
        private set

    fun create() {
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager!!.addView(getView(), getParams())
    }

    fun getParams(): WindowManager.LayoutParams {
        if (params == null) params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            windowType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        return params as WindowManager.LayoutParams
    }

    fun getView(): View {
        if (view == null) view = View.inflate(context, layoutRes, null)
        return view!!
    }

    // Set to TYPE_SYSTEM_ALERT so that the Service can display it
    private val windowType: Int
        get() = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY

    fun destroy() {
        try {
            if (windowManager != null) if (view != null) windowManager!!.removeViewImmediate(view)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } finally {
            params = null
            view = null
            windowManager = null
        }
    }
}