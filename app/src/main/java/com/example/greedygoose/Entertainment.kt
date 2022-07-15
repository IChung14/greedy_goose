package com.example.greedygoose;

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import com.example.greedygoose.databinding.EntertainmentBinding
import android.app.AlertDialog;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import com.example.greedygoose.foreground.FloatingService


//TODO: consider converting it into a fragment and get viewModel from mainActivity
class Entertainment : AppCompatActivity() {

    private lateinit var binding: EntertainmentBinding

    private var resultLauncher =
        registerForActivityResult(StartActivityForResult()) { bindFloatingService() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EntertainmentBinding.inflate(layoutInflater)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        checkOverlayPermission()
        setContentView(binding.root)

        bindFloatingService()
    }

    private fun bindFloatingService(){
        if (Settings.canDrawOverlays(this)) {

            val intent = Intent(this, FloatingService::class.java)
            this.startService(intent)
//            mod.setEntertainment(true)
//            mod.observeEntertainment(this, this)
        }
    }

    // method to ask user to grant the Overlay permission
    private fun checkOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            // show a pop-up indicating they should set permissions for the app
            val permissionAlert = AlertDialog.Builder(this)
            permissionAlert.setMessage("For the application to run, please turn on permissions for Greedy Goose")
            permissionAlert.setTitle("Permission Reminder")
            permissionAlert.setPositiveButton("Ok") { _, _ ->
                resultLauncher.launch(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
            }
            permissionAlert.setCancelable(true)
            permissionAlert.create().show()
        }
    }
}