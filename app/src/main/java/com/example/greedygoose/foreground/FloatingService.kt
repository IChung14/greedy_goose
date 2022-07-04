package com.example.greedygoose.foreground

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
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
import android.view.WindowManager
import com.example.greedygoose.foreground.movementModule.DragtoEatModule


class FloatingService : Service() {

    // Binder given to clients
    private val binder = FloatingServiceBinder()

    inner class FloatingServiceBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): FloatingService = this@FloatingService
    }

    // This FloatingGoose holds 1 floating entity
    lateinit var floatingGoose: FloatingGoose
    lateinit var floatingEgg: FloatingEgg
    lateinit var floatingFood: FloatingEgg

    var job: Job? = null
    val scope = MainScope()
    var first = true

    override fun onBind(intent: Intent): IBinder? {
        job = scope.launch {
            floatingGoose =
                FloatingGoose(this@FloatingService)         // construct a floating object
                    .setMovementModule {                      // making it responsive
                        DragMovementModule(
                            it.params,
                            it.binding.rootContainer,       // this is the view that will listen to drags
                            it.windowManager,
                            it.binding.root,
                            this@FloatingService
                        )
                    }
                    .build()

            while (true) {
                if (screenOn()) {
                    // use percentage to determine whether to lay an egg
                    val chance = Random().nextInt(10)
                    if (chance < 2 || first) {
                        first = false
                        val goose_params = floatingGoose.getLocation()
                        floatingEgg = FloatingEgg(this@FloatingService)
                            .setWindowLayoutParams(goose_params!!.x, goose_params.y)
                            .setMovementModule {
                                TouchDeleteModule(
                                    it.params,
                                    it.binding.rootContainer,
                                    it.windowManager,
                                    it.binding.root
                                )
                            }
                            .build()
                        floatingEgg.windowModule.binding.gooseImg.setImageResource(R.drawable.egg_small)
                        delay(5000)
                    } else if (chance > 7) {
                        floatingFood = FloatingEgg(this@FloatingService)
                            .setWindowLayoutParams(
                                Random().nextInt(2000) - 1000,
                                Random().nextInt(2000) - 1000)
                            .setMovementModule {
                                DragtoEatModule(
                                    it.params,
                                    it.binding.rootContainer,
                                    it.windowManager,
                                    it.binding.root,
                                    floatingGoose
                                )
                            }
                            .build()
                        floatingFood.windowModule.binding.gooseImg.setImageResource(R.drawable.bbt)
                        delay(5000)
                    } else {
                        delay(5000)
                    }

                }
            }
        }
        return binder
    }

    fun screenOn(): Boolean {
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        return powerManager.isInteractive
    }

    override fun onDestroy() {
        floatingGoose.destroy()
        floatingEgg.destroy()
        super.onDestroy()
    }
}


//class ForegroundService : Service() {
//    override fun onBind(intent: Intent): IBinder? {
//        throw UnsupportedOperationException("Not yet implemented")
//    }
//
//    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
//        // create the custom or default notification
//        // based on the android version
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startMyOwnForeground() else startForeground(
//            1,
//            Notification()
//        )
//
//        // create an instance of Window class
//        // and display the content on screen
//        val window = Window(this)
//        window.open()
//
//
//        return super.onStartCommand(intent, flags, startId)
//    }
//
//    // for android version >=O we need to create
//    // custom notification stating
//    // foreground service is running
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun startMyOwnForeground() {
//        val NOTIFICATION_CHANNEL_ID = "example.permanence"
//        val channelName = "Background Service"
//        val chan = NotificationChannel(
//            NOTIFICATION_CHANNEL_ID,
//            channelName,
//            NotificationManager.IMPORTANCE_MIN
//        )
//        val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
//        manager.createNotificationChannel(chan)
//        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
//        val notification = notificationBuilder.setOngoing(true)
//            .setContentTitle("Service running")
//            .setContentText("Displaying over other apps") // this is important, otherwise the notification will show the way
//            // you want i.e. it will show some default notification
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setPriority(NotificationManager.IMPORTANCE_MIN)
//            .setCategory(Notification.CATEGORY_SERVICE)
//            .build()
//        startForeground(2, notification)
//    }
//}
