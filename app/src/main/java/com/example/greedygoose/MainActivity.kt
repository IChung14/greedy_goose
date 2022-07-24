package com.example.greedygoose

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.Html
import androidx.activity.result.ActivityResultLauncher
import com.example.greedygoose.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        val greedyGooseText = binding.textView
        val greedy = getColoredSpanned("Greedy Goose", "#e83372", "#E191CA")
        greedyGooseText.text = Html.fromHtml(greedy)
        greedyGooseText.textSize = 90F

        val entertainmentButton = binding.entertainmentButton
        entertainmentButton.setOnClickListener {
            startActivity(Intent(this, EntertainmentPage::class.java))
        }

        val productivityButton = binding.productivityButton
        productivityButton.setOnClickListener {
            startActivity(Intent(this, TimerPage::class.java))
        }

        val settingsButton = binding.settingsButton
        settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsPage::class.java))
        }

        setContentView(binding.root)
    }

    private fun getColoredSpanned(text: String, color1: String, color2: String): String {
        var html = ""
        var color = color1
        for (element in text) {
            if (element != ' ') {
                color = if (color == color1) color2 else color1
            }
            html += "<font color=$color><b>$element</b></font>"
        }
        return html
    }

    companion object{

        // method to ask user to grant the Overlay permission
        fun checkOverlayPermission(context: Context, resultLauncher: ActivityResultLauncher<Intent>) {
            if (!Settings.canDrawOverlays(context)) {
                // show a pop-up indicating they should set permissions for the app
                val permissionAlert = AlertDialog.Builder(context)
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
}