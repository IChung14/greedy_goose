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


class FloatingService : Service() {

    // Binder given to clients
    private val binder = FloatingServiceBinder()

    inner class FloatingServiceBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): FloatingService = this@FloatingService
    }

    // This FloatingGoose holds 1 floating entity
    lateinit var floatingGoose : FloatingGoose
    lateinit var floatingEgg : FloatingEgg
    var job: Job? = null
    val scope = MainScope()

    override fun onBind(intent: Intent): IBinder? {

        // TODO: THIS IS A DEMO CODE
        //  This piece of code demonstrates that multiple FloatingGoose creates
        //  multiple overlay views that moves around independently


        floatingGoose = FloatingGoose(this)         // construct a floating object
            .setMovementModule {                      // making it responsive
                DragMovementModule(
                    it.params,
                    it.binding.rootContainer,       // this is the view that will listen to drags
                    it.windowManager,
                    it.binding.root
                )
            }
            .build()
        job = scope.launch{
            while(true) {
                val goose_params = floatingGoose.getLocation()
                floatingEgg = FloatingEgg(this@FloatingService)
                    .setWindowLayoutParams(goose_params!!.x, goose_params.y)
                    .setMovementModule {                      // making it responsive
                        TouchDeleteModule(
                            it.params,
                            it.binding.rootContainer,       // this is the view that will listen to drags
                            it.windowManager,
                            it.binding.root
                        )
                    }
                    .build()
                floatingEgg.windowModule.binding.gooseImg.setImageResource(R.drawable.egg_small)
                delay(5000)

            }}
        return binder
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
