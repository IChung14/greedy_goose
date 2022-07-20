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
class EntertainmentPage : AppCompatActivity() {

    private lateinit var binding: EntertainmentBinding

    private var resultLauncher =
        registerForActivityResult(StartActivityForResult()) { startFloatingService() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EntertainmentBinding.inflate(layoutInflater)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        checkOverlayPermission()
        setContentView(binding.root)

        startFloatingService()

        val stopButton = binding.stopEntBtn
        stopButton.setOnClickListener {
            val intent = Intent(this, FloatingService::class.java)
            intent.putExtra("flags", 1)
            intent.putExtra("entertainment", 1)
            this.startService(intent)
        }
    }

    private fun startFloatingService(){
        if (Settings.canDrawOverlays(this)) {
            val intent = Intent(this, FloatingService::class.java)
            intent.putExtra("flags", 3)
            intent.putExtra("entertainment", 1)
            this.startService(intent)
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