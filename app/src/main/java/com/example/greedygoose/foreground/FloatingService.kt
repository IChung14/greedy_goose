package com.example.greedygoose.foreground

import android.content.Intent
import android.graphics.PixelFormat
import android.os.Binder
import android.os.IBinder
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
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


class FloatingService : LifecycleService () {

    private lateinit var viewModel: FloatingViewModel

    lateinit var floatingGoose: FloatingComponent
    var floatingEgg: FloatingComponent? = null
    lateinit var floatingFood: FloatingComponent
    lateinit var floatingWindow: FloatingComponent
    var isAngry = false

    var isRunning = false

    override fun onCreate() {
        super.onCreate()
        viewModel = FloatingViewModel(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val wasAngry = isAngry
        isAngry = intent?.getBooleanExtra("angry", false) == true

        // avoid running multiple geese
        if(!isRunning){
            if(!isAngry){
                // run entertainment goose protocol
                entertainmentGoose()
            }else{
                TODO("EXECUTE ANGRY GOOSE PROTOCOL!")
            }
        }else if(isAngry && !wasAngry){
            TODO("MAKE EXISTING GOOSE ANGRY!")
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun entertainmentGoose(){

        // construct a floating object
        floatingGoose = FloatingComponent(
            this@FloatingService,
            "GOOSE",
            viewModel)
            .setMovementModule {                      // making it responsive
                DragMovementModule(
                    it.params,
                    it.binding.rootContainer,       // this is the view that will listen to drags
                    it.windowManager,
                    it.binding.root,
                    this,
                    viewModel
                )
            }
            .build()
        viewModel.theme.observe(this){
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
                        floatingEgg = FloatingComponent(this@FloatingService, "EGG",
                            viewModel)
                            .setImageResource(R.drawable.egg_small)
                            .setWindowLayoutParams(floatingGoose.getLocation()!!)
                            .setMovementModule {
                                TouchDeleteModule(
                                    it.params,
                                    it.binding.rootContainer,
                                    it.windowManager,
                                    it.binding.root,
                                    viewModel
                                )
                            }
                            .build()
                        floatingEgg?.delete_egg()
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
                    var x = Random().nextInt(1000) - 500
                    var y = Random().nextInt(1000) - 500
                    floatingFood = FloatingComponent(this@FloatingService, "FOOD",
                        viewModel)
                        .setImageResource(R.drawable.bbt)
                        .setWindowLayoutParams(x, y)
                        .setMovementModule {
                            DragToEatModule(
                                it.params,
                                it.binding.rootContainer,
                                it.windowManager,
                                it.binding.root,
                                floatingGoose
                            )
                        }
                    floatingFood.build()
                    floatingFood.delete_food()
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
                var chance = Random().nextInt(10)
                if (screenOn() && chance > 6 && floatingGoose.movementModule!!.isDraggable) {
                    val goose_params = floatingGoose.getLocation()
                    var gx = goose_params!!.x
                    if (gx > 250 || gx <= 50) {
                        floatingGoose.movementModule!!.is_dragged = true
                        floatingGoose.movementModule!!.isDraggable = false

                        if (gx > 250) {
                            (floatingGoose.movementModule!! as DragMovementModule)
                                .walkOffScreen("RIGHT")
                        } else {
                            (floatingGoose.movementModule!! as DragMovementModule)
                                .walkOffScreen("LEFT")
                        }


                        var windowParams: LayoutParams = LayoutParams(
                            LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT,
                            LayoutParams.TYPE_APPLICATION_OVERLAY,
                            LayoutParams.FLAG_NOT_FOCUSABLE or LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                            PixelFormat.TRANSLUCENT
                        )

                        if (gx <= 50) {
                            windowParams.x = -1080
                        } else {
                            windowParams.x = 1080
                        }
                        windowParams.y = floatingGoose.getLocation()!!.y
                        var memeChance = Random().nextInt(7)
                        var meme = memes[memeChance]

                        floatingWindow = FloatingComponent(this@FloatingService, "WINDOW",
                            viewModel)
                            .setImageResource(meme)
                            .setWindowLayoutParams(windowParams)
                            .setMovementModule {
                                PopUpWindowModule(
                                    windowParams,
                                    it.binding.rootContainer,
                                    it.windowManager,
                                    it.binding.root
                                )
                            }
                            .build()
                        delay(2700)

                        if (gx <= 50) {

                            (floatingGoose.movementModule!! as DragMovementModule)
                                .randomWalk(floatingGoose.windowModule, true, false, Direction.LEFT)
                            floatingWindow.movementModule!!.startAction(null, false, Direction.LEFT)

                        } else {
                            (floatingGoose.movementModule!! as DragMovementModule)
                                .randomWalk(floatingGoose.windowModule, true, false, Direction.RIGHT)
                            floatingWindow.movementModule!!.startAction(null, false, Direction.RIGHT)

                        }


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
        floatingEgg!!.destroy()
        super.onDestroy()
    }
}