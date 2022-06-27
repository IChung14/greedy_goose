package com.example.greedygoose;

import android.app.*
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import com.example.greedygoose.databinding.EntertainmentBinding
import com.example.greedygoose.foreground.FloatingLayout
import android.content.DialogInterface
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer


//TODO: consider converting it into a fragment and get viewModel from mainActivity
class Entertainment : AppCompatActivity() {

    private lateinit var binding: EntertainmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EntertainmentBinding.inflate(layoutInflater)

        checkOverlayPermission()
        setContentView(binding.root)

        if (Settings.canDrawOverlays(this)) {
            mod.set_entertainment(true)
        }
        mod.observe_entertainment(this, this)

        val backButton = binding.backImageButton
        backButton.setOnClickListener {
            startActivity(Intent(this@Entertainment, MainActivity::class.java))
        }

    }

    // method to ask user to grant the Overlay permission
    private fun checkOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            // show a pop-up indicating they should set permissions for the app
            val permissionAlert = AlertDialog.Builder(this)
            permissionAlert.setMessage("For the application to run, please turn on permissions for Greedy Goose")
            permissionAlert.setTitle("Permission Reminder")
            permissionAlert.setPositiveButton("Ok",
                DialogInterface.OnClickListener { dialog, which ->
                    // send user to the device settings
                    val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    startActivity(myIntent)
                })
            permissionAlert.setCancelable(true)
            permissionAlert.create().show()
        }
    }
}