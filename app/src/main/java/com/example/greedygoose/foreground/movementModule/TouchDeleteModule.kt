package com.example.greedygoose.foreground.movementModule

import com.example.greedygoose.data.Direction
import com.example.greedygoose.foreground.FloatingViewModel
import com.example.greedygoose.foreground.ui.FloatingWindowModule

enum class ObjectType {EGG, PRINTS}

class TouchDeleteModule (
    private val viewModel: FloatingViewModel,
    windowModule: FloatingWindowModule,
    type: ObjectType
): MovementModule(windowModule) {
    var type = type

    override fun startAction(round: Boolean, dir: Direction) {}

    override fun run() {
        rootContainer.setOnClickListener{
            destroy()
            if (type == ObjectType.EGG) {
                viewModel.incrementEggCount()
            }
        }
    }
}