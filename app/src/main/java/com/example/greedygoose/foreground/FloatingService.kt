package com.example.greedygoose.foreground

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Binder
import android.os.IBinder
import android.os.PowerManager
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import com.example.greedygoose.R
import com.example.greedygoose.foreground.movementModule.DragMovementModule
import com.example.greedygoose.foreground.movementModule.DragToEatModule
import com.example.greedygoose.foreground.movementModule.PopUpWindowModule
import com.example.greedygoose.foreground.movementModule.TouchDeleteModule
import com.example.greedygoose.memes
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


class FloatingService : Service() {

    private val binder = FloatingServiceBinder()        // Binder given to clients
    lateinit var floatingGoose : FloatingComponent
    var floatingEgg : FloatingComponent? = null
    lateinit var floatingFood: FloatingComponent
    lateinit var floatingWindow: FloatingComponent

    override fun onBind(intent: Intent): IBinder {
        // construct a floating object
        floatingGoose = FloatingComponent(this@FloatingService, "GOOSE")
            .setMovementModule {                      // making it responsive
                DragMovementModule(
                    it.params,
                    it.binding.rootContainer,       // this is the view that will listen to drags
                    it.windowManager,
                    it.binding.root,
                    this
                )
            }
            .build()
        layEggs()
        formFoods()
        dragWindow()
        return binder
    }

    private fun layEggs(){
        MainScope().launch{
            var chance = 4
            while(true) {
                // use percentage to determine whether to lay an egg
                if(chance < 3 && screenOn()){
                    floatingEgg = FloatingComponent(this@FloatingService, "EGG")
                        .setImageResource(R.drawable.egg_small)
                        .setWindowLayoutParams(floatingGoose.getLocation()!!)
                        .setMovementModule {
                            TouchDeleteModule(
                                it.params,
                                it.binding.rootContainer,
                                it.windowManager,
                                it.binding.root
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

    private fun formFoods(){
        MainScope().launch {
            var chance = 1
            while (true) {
                // use percentage to determine whether to create a food item
                if(chance > 7 && screenOn()) {
                    var x = Random().nextInt(1000) - 500
                    var y = Random().nextInt(1000) - 500
                    floatingFood = FloatingComponent(this@FloatingService, "FOOD")
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

    private fun dragWindow(){
        MainScope().launch{
            while(true) {
                // use percentage to determine whether to drag a window out
                var chance = Random().nextInt(2)

                if(screenOn() && chance == 1){
                    val goose_params = floatingGoose.getLocation()
                    if (goose_params!!.x <= 50) {
                        if (floatingGoose.movementModule!!.isDraggable) {
                            floatingGoose.movementModule!!.is_dragged = true
                            floatingGoose.movementModule!!.isDraggable = false
                            (floatingGoose.movementModule!! as DragMovementModule)
                                .walkOffScreen(floatingGoose.windowModule)
                            var windowParams: LayoutParams = LayoutParams(
                                LayoutParams.WRAP_CONTENT,
                                LayoutParams.WRAP_CONTENT,
                                LayoutParams.TYPE_APPLICATION_OVERLAY,
                                LayoutParams.FLAG_NOT_FOCUSABLE or LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                                PixelFormat.TRANSLUCENT
                            )
                            windowParams.x = -1080
                            windowParams.y = floatingGoose.getLocation()!!.y
                            var memeChance = Random().nextInt(7)
                            var meme = memes[memeChance]

                            floatingWindow = FloatingComponent(this@FloatingService, "WINDOW")
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

                            (floatingGoose.movementModule!! as DragMovementModule)
                                .randomWalk(floatingGoose.windowModule, true, false, floatingWindow.windowModule)
                            delay(85)
                            floatingWindow.movementModule!!.start_action()
                        }
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

    inner class FloatingServiceBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): FloatingService = this@FloatingService
    }
}