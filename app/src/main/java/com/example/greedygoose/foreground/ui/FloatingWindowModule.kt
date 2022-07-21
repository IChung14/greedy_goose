package com.example.greedygoose.foreground.ui

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.annotation.DrawableRes
import com.example.greedygoose.databinding.FloatingLayoutBinding

class FloatingWindowModule(
    context: Context,
    @DrawableRes private var imgRes: Int? = null,
    var params: WindowManager.LayoutParams = defaultParam()
) {
    var binding: FloatingLayoutBinding
        private set
    var windowManager: WindowManager

    // Set to TYPE_SYSTEM_ALERT so that the Service can display it

    init {
        params.gravity = Gravity.CENTER;
        binding = FloatingLayoutBinding.inflate(LayoutInflater.from(context))

        // default image is the goose
        imgRes?.let { binding.gooseImg.setImageResource(it) }
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.addView(binding.root, params)
    }

    fun destroy() {
        try {
            windowManager.removeViewImmediate(binding.root)
        } catch (e: IllegalArgumentException) {}
    }

    companion object{
        private val windowType: Int
            get() = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY

        fun defaultParam(): WindowManager.LayoutParams{
            return WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                windowType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
        }
    }
}