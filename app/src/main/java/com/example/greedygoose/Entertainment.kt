package com.example.greedygoose;

import android.app.*
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import com.example.greedygoose.databinding.EntertainmentBinding
import com.example.greedygoose.foreground.FloatingLayout
import com.example.greedygoose.foreground.FloatingListener


class Entertainment() : Activity() {
    private lateinit var binding: EntertainmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EntertainmentBinding.inflate(layoutInflater)
        setContentView(R.layout.entertainment)

        startService()

    }


    // method for starting the service
    private fun startService() {
        // check if the user has already granted
        // the Draw over other apps permission
        checkOverlayPermission()
        if (Settings.canDrawOverlays(this)) {
            // start the service based on the android version
            val floatingListener: FloatingListener = object : FloatingListener {
                override fun onCreateListener(view: View?) {}
                override fun onCloseListener() {}
            }

            val floatingLayout = FloatingLayout(this, R.layout.floating_layout)
            floatingLayout.setFloatingListener(floatingListener)
            floatingLayout.create()
        }
    }

    // method to ask user to grant the Overlay permission
    private fun checkOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            // send user to the device settings
            val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            startActivity(myIntent)
        }
    }
}