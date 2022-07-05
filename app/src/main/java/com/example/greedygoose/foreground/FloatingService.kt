package com.example.greedygoose.foreground

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.example.greedygoose.R
import com.example.greedygoose.foreground.movementModule.TouchDeleteModule
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import android.os.PowerManager
import com.example.greedygoose.foreground.movementModule.DragMovementModule
import com.example.greedygoose.foreground.movementModule.DragToEatModule


class FloatingService : Service() {

    // Binder given to clients
    private val binder = FloatingServiceBinder()

    // This FloatingGoose holds 1 floating entity
    lateinit var floatingGoose : FloatingComponent
    lateinit var floatingEgg : FloatingComponent
    lateinit var floatingFood: FloatingComponent

    override fun onBind(intent: Intent): IBinder {
        floatingGoose = FloatingComponent(this@FloatingService, "GOOSE")         // construct a floating object
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
        return binder
    }

    private fun layEggs(){
        MainScope().launch{
            var chance = 1
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
                if(chance > 7 && screenOn()) {
                    floatingFood = FloatingComponent(this@FloatingService, "FOOD")
                        .setImageResource(R.drawable.bbt)
                        .setWindowLayoutParams(
                            Random().nextInt(2000) - 1000,
                            Random().nextInt(2000) - 1000
                        )
                        .setMovementModule {
                            DragToEatModule(
                                it.params,
                                it.binding.rootContainer,
                                it.windowManager,
                                it.binding.root,
                                floatingGoose
                            )
                        }
                        .build()
                }
                delay(5000)

                chance = Random().nextInt(10)
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

    inner class FloatingServiceBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): FloatingService = this@FloatingService
    }
}
