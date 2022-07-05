package com.example.greedygoose.foreground

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.example.greedygoose.R
import com.example.greedygoose.foreground.movementModule.TouchDeleteModule
import kotlinx.coroutines.Job
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

    inner class FloatingServiceBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): FloatingService = this@FloatingService
    }

    // This FloatingGoose holds 1 floating entity
    lateinit var floatingGoose : FloatingComponent
    lateinit var floatingEgg : FloatingComponent
    lateinit var floatingFood: FloatingComponent

    override fun onBind(intent: Intent): IBinder? {
        floatingGoose = FloatingComponent(this@FloatingService)         // construct a floating object
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
        var chance = 1
        MainScope().launch{
            while(true) {
                // use percentage to determine whether to lay an egg
//                if(chance < 3 && screenOn()){

                if(chance < 3 && screenOn()){
                    floatingEgg = FloatingComponent(this@FloatingService)
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
        var chance = 1
        MainScope().launch {
            while (true) {
                if(chance > 7 && screenOn()) {
                    floatingFood = FloatingComponent(this@FloatingService)
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
                    floatingFood.windowModule.binding.gooseImg.setImageResource(R.drawable.bbt)
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
}
