package com.example.greedygoose.foreground

import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver

class FloatingResult(handler: Handler?, private val floatingListener: FloatingListener?) :
    ResultReceiver(handler) {
    override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
        if (floatingListener != null) {
            if (resultCode == FloatingComponent.ACTION_ON_CREATE) {
                if (FloatingService.view != null) floatingListener.onCreateListener(FloatingService.view)
            }
            if (resultCode == FloatingComponent.ACTION_ON_CLOSE) {
                floatingListener.onCloseListener()
            }
        }
        super.onReceiveResult(resultCode, resultData)
    }
}