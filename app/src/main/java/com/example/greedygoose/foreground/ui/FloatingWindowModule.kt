package com.example.greedygoose.foreground.ui

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.annotation.LayoutRes
import com.example.greedygoose.databinding.FloatingLayoutBinding

class FloatingWindowModule(private val context: Context) {
    var params: WindowManager.LayoutParams? = null
    lateinit var binding: FloatingLayoutBinding
        private set
    lateinit var windowManager: WindowManager

    fun create() {
        if(params == null) params = defaultParam()
        binding = FloatingLayoutBinding.inflate(LayoutInflater.from(context))
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.addView(binding.root, params)
    }

    fun defaultParam(): WindowManager.LayoutParams{
        return WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            windowType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
    }

    // Set to TYPE_SYSTEM_ALERT so that the Service can display it
    private val windowType: Int
        get() = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY

    fun destroy() {
        try {
            windowManager.removeViewImmediate(binding.root)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } finally {
            params = null
        }
    }
}