package com.example.greedygoose.foreground.movementModule

import android.view.View
import android.view.WindowManager
import com.example.greedygoose.data.Direction
import com.example.greedygoose.foreground.FloatingViewModel
import com.example.greedygoose.foreground.ui.FloatingWindowModule


class TouchDeleteModule (
    private val viewModel: FloatingViewModel,
    windowModule: FloatingWindowModule
): MovementModule(windowModule) {

    override fun startAction(floatingWindowModule: FloatingWindowModule?, round: Boolean, dir: Direction) {}

    override fun run() {
        rootContainer.setOnClickListener{
            destroy()
            viewModel.incrementEggCount()
        }
    }
}