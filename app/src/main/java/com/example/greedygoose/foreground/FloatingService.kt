package com.example.greedygoose.foreground

import android.content.Intent
import android.graphics.PixelFormat
import android.os.PowerManager
import android.view.WindowManager.LayoutParams
import androidx.lifecycle.LifecycleService
import com.example.greedygoose.R
import com.example.greedygoose.data.Direction
import com.example.greedygoose.foreground.movementModule.DragMovementModule
import com.example.greedygoose.foreground.movementModule.DragToEatModule
import com.example.greedygoose.foreground.movementModule.PopUpWindowModule
import com.example.greedygoose.foreground.movementModule.TouchDeleteModule
import com.example.greedygoose.data.memes
import com.example.greedygoose.data.themeMap
import com.example.greedygoose.foreground.ui.*
import com.example.greedygoose.foreground.ui.FloatingWindowModule.Companion.defaultParam
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


class FloatingService : LifecycleService() {

    private lateinit var viewModel: FloatingViewModel
    private lateinit var floatingFactory: FloatingComponentFactory

    lateinit var floatingGoose: FloatingGoose
    lateinit var floatingEgg: FloatingEgg
    lateinit var floatingFood: FloatingFood
    lateinit var floatingWindow: FloatingWindow

    var isAngry = false
    var isRunning = false

    override fun onCreate() {
        super.onCreate()
        viewModel = FloatingViewModel(applicationContext)
        floatingFactory = FloatingComponentFactory(this, viewModel)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val wasAngry = isAngry
        isAngry = intent?.getBooleanExtra("angry", false) == true

        // avoid running multiple geese
        if (!isRunning) {
            if (!isAngry) {
                // run entertainment goose protocol
                entertainmentGoose()
            } else {
                TODO("EXECUTE ANGRY GOOSE PROTOCOL!")
            }
        } else if (isAngry && !wasAngry) {
            TODO("MAKE EXISTING GOOSE ANGRY!")
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun entertainmentGoose() {

        // construct a floating object
        floatingGoose = floatingFactory.createGoose()
        viewModel.theme.observe(this) {
            themeMap[it]?.get(viewModel.action.value)?.let { actionImgSrc ->
                floatingGoose.windowModule.binding.gooseImg.setImageResource(actionImgSrc)
            }
        }

        layEggs()
        formFoods()
        dragWindow()
    }

    private fun layEggs() {
        MainScope().launch {
            if (floatingGoose.movementModule!!.isDraggable) {
                var chance = 4
                while (true) {
                    // use percentage to determine whether to lay an egg
                    if (chance < 3 && screenOn()) {
                        floatingGoose.getLocation()?.let {
                            floatingEgg = floatingFactory.createEgg(it)
                            floatingEgg.expireEgg()
                        }
                    }
                    delay(5000)
                    chance = Random().nextInt(10)
                }
            }
        }
    }

    private fun formFoods() {
        MainScope().launch {
            var chance = 1
            while (true) {
                // use percentage to determine whether to create a food item
                if (chance > 7 && screenOn()) {
                    floatingFood = floatingFactory.createFood(floatingGoose)
                    floatingFood.expireFood()
                }
                delay(5000)
                chance = Random().nextInt(10)
            }
        }
    }

    private fun dragWindow() {
        MainScope().launch {
            while (true) {
                // use percentage to determine whether to drag a window out
                val chance = Random().nextInt(10)
                if (screenOn() && chance > 6 && floatingGoose.movementModule!!.isDraggable) {
                    val gooseParams = floatingGoose.getLocation()
                    val gx = gooseParams!!.x
                    if (gx > 250 || gx <= 50) {
                        val gooseMM = floatingGoose.movementModule!! as DragMovementModule
                        gooseMM.isDragged = true
                        gooseMM.isDraggable = false

                        gooseMM.walkOffScreen(if (gx > 250) "RIGHT" else "LEFT")

                        floatingWindow = floatingFactory.createWindow(floatingGoose.getLocation()!!)

                        delay(2700)

                        val walkDirection = if (gx <= 50) Direction.LEFT else Direction.RIGHT
                        gooseMM.randomWalk(
                            floatingGoose.windowModule,
                            is_meme = true,
                            round = false,
                            dir = walkDirection
                        )
                        floatingWindow.movementModule?.startAction(null, false, walkDirection)

                        delay(85)

                    }
                }
                delay(5000)
            }
        }
    }

    private fun screenOn(): Boolean {
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        return powerManager.isInteractive
    }

    override fun onDestroy() {
        floatingGoose.destroy()
        floatingEgg.destroy()
        super.onDestroy()
    }
}