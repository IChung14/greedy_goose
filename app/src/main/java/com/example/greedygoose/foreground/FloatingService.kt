package com.example.greedygoose.foreground

import android.content.Intent
import android.os.PowerManager
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.example.greedygoose.data.*
import com.example.greedygoose.foreground.movementModule.DragMovementModule
import com.example.greedygoose.foreground.movementModule.DragToEatModule
import com.example.greedygoose.foreground.ui.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


class FloatingService : LifecycleService() {

    private lateinit var viewModel: FloatingViewModel
    private lateinit var floatingFactory: FloatingComponentFactory

    private lateinit var floatingGoose: FloatingGoose
    private lateinit var floatingEgg: FloatingEgg
    private lateinit var floatingFood: FloatingFood
    private lateinit var floatingWindow: FloatingWindow
    private lateinit var floatingPrints: FloatingPrints

    var isRunning = false
    private val globalFlag: MutableLiveData<GooseState> = MutableLiveData(GooseState.NONE)

    override fun onCreate() {
        super.onCreate()
        viewModel = FloatingViewModel(applicationContext)
        floatingFactory = FloatingComponentFactory(this, viewModel)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // Based on the value of hte intent flag, set the globalFlag to represent the correct
        // goose enum state
        val state = intent?.getIntExtra("flags", GooseState.NONE.ordinal)!!
        val isEntertainment = intent.getIntExtra("entertainment", 0)

        globalFlag.value = if (state == 1 && globalFlag.value != GooseState.ENT_GOOSE) {
            GooseState.KILL_GOOSE
        } else if (state == 2) {
            GooseState.PROD_GOOSE
        } else if (state == 3 && !(globalFlag.value == GooseState.PROD_GOOSE && isEntertainment == 1)) {
            GooseState.ENT_GOOSE
        } else if (state == 0) {
            GooseState.NONE
        } else {
            globalFlag.value
        }

        if (state == 2) {
            viewModel.action.value = Action.ANGRY_LEFT
        }

        viewModel.appMode.value = globalFlag.value

        if (globalFlag.value == GooseState.KILL_GOOSE) {
            // KILL_GOOSE, when timer is snoozed or killed for good
            // may need to update destroy to cleanly end jobs
            if(::floatingGoose.isInitialized) floatingGoose.destroy()
            if(::floatingWindow.isInitialized) floatingWindow.destroy()
            isRunning = false

        } else if (globalFlag.value == GooseState.PROD_GOOSE || globalFlag.value == GooseState.ENT_GOOSE) {
            // PROD_GOOSE, when timer goes off or is resumed after snoozing
            // ENT_GOOSE, when goose should begin entertainment mode
            runGoose()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun runGoose() {
        if (!isRunning) {
            isRunning = true
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
            makePrints()
        }
    }

    private fun layEggs() {
        MainScope().launch {
            if (floatingGoose.movementModule!!.isDraggable) {
                var chance = 4
                while (globalFlag.value == GooseState.ENT_GOOSE) {
                    // use percentage to determine whether to lay an egg
                    if (chance < 3 && screenOn()) {
                        floatingGoose.getLocation().let {
                            floatingEgg = floatingFactory.createEgg(it)
                            floatingEgg.expireEgg(globalFlag)
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
            while (globalFlag.value == GooseState.ENT_GOOSE) {
                // use percentage to determine whether to create a food item
                if (chance > 7 && screenOn()) {
                    floatingFood = floatingFactory.createFood(floatingGoose)
                    (floatingFood.movementModule as DragToEatModule).setContext(applicationContext)
                    floatingFood.expireFood(globalFlag)
                }
                delay(5000)
                chance = Random().nextInt(10)
            }
        }
    }

    private fun dragWindow() {
        MainScope().launch {
            try {
                while (floatingGoose.movementModule?.isAlive == true) {
                    // use percentage to determine whether to drag a window out
                    val chance = Random().nextInt(10)
                    if (screenOn() && chance > 6 && floatingGoose.movementModule!!.isDraggable) {
                        val gooseParams = floatingGoose.getLocation()
                        val gx = gooseParams.x
                        if (gx > 250 || gx <= 50) {
                            val gooseMM = floatingGoose.movementModule as DragMovementModule?
                            gooseMM?.isDragged = true
                            gooseMM?.isDraggable = false

                            gooseMM?.walkOffScreen(if (gx > 250) "RIGHT" else "LEFT")

                            floatingWindow =
                                floatingFactory.createWindow(floatingGoose.getLocation())

                            delay(2700)

                            print("starting Random walk")
                            val walkDirection = if (gx <= 50) Direction.LEFT else Direction.RIGHT
                            gooseMM?.randomWalk(
                                floatingGoose.windowModule,
                                is_meme = true,
                                round = false,
                                dir = walkDirection
                            )
                            print("done random walk")
                            floatingWindow.movementModule?.startAction(false, walkDirection)

                            delay(85)

                        }
                    }
                    delay(5000)
                }
            } catch (e: IllegalArgumentException) {
                println("The Goose is dead and so are you")
            }

        }
    }

    private fun makePrints() {
        MainScope().launch {
            if (floatingGoose.movementModule!!.isDraggable && !floatingGoose.movementModule!!.isFlying) {
                var chance = 1
                while (true) {
                    // use percentage to determine whether to lay an egg
                    if (screenOn()) {
                        if ((chance > 7 && globalFlag.value == GooseState.ENT_GOOSE) ||
                            (chance > 5 && globalFlag.value == GooseState.PROD_GOOSE)) {
                            floatingGoose.getLocation().let {
                                val gx = it.x
                                val walkDirection =
                                    if (gx <= 50) Direction.LEFT else Direction.RIGHT
                                floatingPrints = floatingFactory.createPrints(it, walkDirection)
                                floatingPrints.expirePrints(globalFlag)
                            }
                        }
                    }
                    delay(5000)
                    chance = Random().nextInt(10)
                }
            }
        }
    }

    private fun screenOn(): Boolean {
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        return powerManager.isInteractive
    }

    override fun onDestroy() {
        if (::floatingGoose.isInitialized) floatingGoose.destroy()
        if (::floatingEgg.isInitialized) floatingEgg.destroy()
        if (::floatingWindow.isInitialized) floatingWindow.destroy()
        super.onDestroy()
    }
}
