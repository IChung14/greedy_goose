package com.example.greedygoose.foreground

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.LifecycleOwner
import com.example.greedygoose.R
import com.example.greedygoose.databinding.FloatingLayoutBinding
import com.example.greedygoose.mod

/**
 * FloatingLayout is owned by MainActivity
 * This class is a communication layer between the MainActivity and FloatingService
 */
class FloatingLayout(private val context: Context, imgName: Int, lifecycle: LifecycleOwner) {

    var isShow = false
        private set

    // you can access FloatingService via this variable
    private lateinit var fService: FloatingService

    /** Defines callbacks for service binding, passed to bindService()  */
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as FloatingService.FloatingServiceBinder
            fService = binder.getService()

            updateView { fRoot ->
                fRoot.gooseImg.setImageResource(imgName)
            }
            set_bind(lifecycle)
        }

        override fun onServiceDisconnected(arg0: ComponentName) {}
    }

    /**
     * Updates a View under FloatingService
     */
    fun updateView(viewModifier: (FloatingLayoutBinding)->Unit){
        viewModifier(fService.floatingEgg.windowModule.binding)
    }

    fun set_bind(life: LifecycleOwner) {
        mod.observe_walk(life, fService.floatingGoose.windowModule.binding.gooseImg)
        mod.observe_theme(life, fService.floatingGoose.windowModule.binding.gooseImg)
    }

    /**
     * Set a View under the context of FloatingService.
     *
     * layoutRes: you are only allowed to send layout id to indicate what object you want to create
     */
    fun setView() {
        isShow = true
        val intent = Intent(context, FloatingService::class.java)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    /**
     * terminate foreground service
     */
    fun destroy() {
        isShow = false
        context.unbindService(connection)
        context.stopService(Intent(context, FloatingService::class.java))
    }

}