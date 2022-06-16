package com.example.greedygoose;

import android.app.*
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import com.example.greedygoose.databinding.EntertainmentBinding
import com.example.greedygoose.foreground.FloatingLayout
import android.content.DialogInterface
import android.app.AlertDialog;
import android.os.Handler
import android.util.DisplayMetrics
import android.view.animation.TranslateAnimation
import java.util.*


//TODO: consider converting it into a fragment and get viewModel from mainActivity
class Entertainment : Activity() {

    private lateinit var binding: EntertainmentBinding
    private val floatingLayout = FloatingLayout(this, R.drawable.egg)

    // store the current coordinates of the goose
    private var gooseX = 0
    private var gooseY = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EntertainmentBinding.inflate(layoutInflater)
        checkOverlayPermission()
        setContentView(binding.root)

        if (Settings.canDrawOverlays(this)) {
            // start the service based on the android version
            floatingLayout.setView()

            // get the goose to walk
            var dp = DisplayMetrics()
            getWindowManager().getDefaultDisplay().getMetrics(dp)
            var h = dp.heightPixels
            var w = dp.widthPixels
            var r = Random()
            var newX = r.nextInt(w)
            var newY = r.nextInt(h)

            var currentX = 0
            var currentY = 0

            // if the goose has never moved before, get the coordinates from the view
            if (gooseX == 0 && gooseY == 0){
                currentX = 500
                currentY = 500
            }
            else{
                currentX = gooseX
                currentY = gooseY
            }

            var anim = TranslateAnimation(currentX.toFloat(), newX.toFloat(),
                currentY.toFloat(), newY.toFloat())
            anim.setDuration(1000);
            anim.setFillAfter(true);

            // update the positioning of the goose
            currentX = newX
            currentY = newY
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