package com.example.greedygoose.foreground.ui

import android.os.ResultReceiver
import android.view.WindowManager
import androidx.lifecycle.LifecycleService
import com.example.greedygoose.R
import com.example.greedygoose.data.Direction
import com.example.greedygoose.data.memes
import com.example.greedygoose.foreground.FloatingViewModel
import com.example.greedygoose.foreground.movementModule.*
import java.util.*

class FloatingComponentFactory(
    private val context: LifecycleService,
    private val viewModel: FloatingViewModel
) {

    fun createGoose(): FloatingGoose{
        val windowModule = FloatingWindowModule(context)
        val movementModule = DragMovementModule(
            context,
            viewModel,
            windowModule
        )
        return FloatingGoose(windowModule, movementModule)
    }

    fun createEgg(gooseParams: WindowManager.LayoutParams, receiver: ResultReceiver? = null): FloatingEgg{
        val windowModule = FloatingWindowModule(context, R.drawable.egg_small, gooseParams)
        val movementModule = TouchDeleteModule(viewModel, windowModule, ObjectType.EGG)
        return FloatingEgg(context, windowModule, movementModule, receiver)
    }

    fun createFood(
        floatingGoose: FloatingGoose,
        receiver: ResultReceiver? = null
    ): FloatingFood{

        val layoutParams = FloatingWindowModule.defaultParam()
        layoutParams.x = Random().nextInt(1000) - 500
        layoutParams.y = Random().nextInt(1000) - 500

        val windowModule = FloatingWindowModule(context, R.drawable.bbt, layoutParams)
        val movementModule = DragToEatModule(floatingGoose, windowModule)

        return FloatingFood(context, viewModel, windowModule, movementModule, receiver)
    }

    fun createWindow(gooseParams: WindowManager.LayoutParams): FloatingWindow{
        val meme = memes[Random().nextInt(7)]

        val windowParams = FloatingWindowModule.defaultParam()
        windowParams.flags = windowParams.flags or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        windowParams.x = if (gooseParams.x <= 50) -1080 else 1080
        windowParams.y = gooseParams.y

        val windowModule = FloatingWindowModule(context, meme, windowParams)
        val movementModule = PopUpWindowModule(windowModule)

        return FloatingWindow(windowModule, movementModule)
    }

    fun createPrints(gooseParams: WindowManager.LayoutParams, dir: Direction, receiver: ResultReceiver? = null): FloatingPrints{
         var windowModule = if(dir == Direction.RIGHT){
            FloatingWindowModule(context, R.drawable.prints_right, gooseParams)
        } else{
            FloatingWindowModule(context, R.drawable.prints_left, gooseParams)
        }
        val movementModule = TouchDeleteModule(viewModel, windowModule, ObjectType.PRINTS)

        return FloatingPrints(context, windowModule, movementModule, receiver)
    }
}