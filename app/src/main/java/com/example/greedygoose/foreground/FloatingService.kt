package com.example.greedygoose.foreground

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.ResultReceiver
import android.view.View

class FloatingService : Service() {
    private var floatingComponent: FloatingComponent? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val layoutRes = intent.getIntExtra(EXTRA_LAYOUT_RESOURCE, -1)
        val receiver: ResultReceiver? = intent.getParcelableExtra(EXTRA_RECEIVER)
        floatingComponent = FloatingComponent(layoutRes, this)
        floatingComponent?.let {
            if (receiver != null) it.setReceiver(receiver)
            it.setUp()
            view = it.getFloatingWindowModule()?.getView()
        }
        return START_STICKY_COMPATIBILITY
    }

    override fun onDestroy() {
        floatingComponent?.destroy()
        super.onDestroy()
    }

    companion object {
        const val EXTRA_LAYOUT_RESOURCE = "extra_layout_resource"
        const val EXTRA_RECEIVER = "extra_receiver"

        // TODO: Memory leak
        var view: View? = null
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
