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

    override fun onBind(intent: Intent): IBinder? {
        floatingGoose = FloatingComponent(this@FloatingService)         // construct a floating object
            .setMovementModule {                      // making it responsive
                DragMovementModule(
                    it.params,
                    it.binding.rootContainer,       // this is the view that will listen to drags
                    it.windowManager,
                    it.binding.root
                )
            }
            .build()
        layEggs()
        return binder
    }

    private fun layEggs(){
        var chance = 1
        MainScope().launch{
            while(true) {
                // use percentage to determine whether to lay an egg
                if(chance < 3){
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

    override fun onDestroy() {
        floatingGoose.destroy()
        floatingEgg.destroy()
        super.onDestroy()
    }
}
