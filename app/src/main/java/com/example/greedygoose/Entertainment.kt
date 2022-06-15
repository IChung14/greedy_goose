package com.example.greedygoose;

import android.app.*
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import com.example.greedygoose.databinding.EntertainmentBinding
import com.example.greedygoose.foreground.FloatingLayout
import com.example.greedygoose.foreground.FloatingListener

//TODO: consider converting it into a fragment and get viewModel from mainActivity
class Entertainment : Activity() {

    private lateinit var binding: EntertainmentBinding
    private val floatingLayout = FloatingLayout(this)
    private var gooseMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EntertainmentBinding.inflate(layoutInflater)

        binding.stopGooseBtn.setOnClickListener {
            floatingLayout.destroy()
        }
        binding.alterGooseBtn.setOnClickListener {
            floatingLayout.updateView { fRoot ->
                fRoot.gooseImg.setImageResource(
                    if (gooseMode) R.drawable.alter_tie_goose else R.drawable.tie_goose
                )
                gooseMode = !gooseMode
            }
        }

        startService()

        setContentView(binding.root)
    }


    // method for starting the service
    private fun startService() {
        // check if the user has already granted
        // the Draw over other apps permission
        checkOverlayPermission()
        if (Settings.canDrawOverlays(this)) {
            // start the service based on the android version
            floatingLayout.setView()
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