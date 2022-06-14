package com.example.greedygoose.foreground

import android.view.View

interface FloatingListener {
    fun onCreateListener(view: View?)
    fun onCloseListener()
}