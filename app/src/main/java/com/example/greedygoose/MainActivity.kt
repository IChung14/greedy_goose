package com.example.greedygoose

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.Html
import android.widget.TextView
import com.example.greedygoose.databinding.ActivityMainBinding
import android.text.Spanned

import android.graphics.Typeface

import android.text.style.StyleSpan

import android.text.SpannableString




class MainActivity : AppCompatActivity() {
//    var relativeLayout = (.findViewById(R.id.homeview2) as RelativeLayout)
//    TextView txt = (TextView)findViewById(R.id.textView)

    lateinit var binding: ActivityMainBinding
    private fun getColoredSpanned(text: String, color1: String, color2: String): String? {
        var html = ""
        var color = color1
        for (i in 0..text.length-1) {
            val character = text[i]
            if (character != ' ') {
                color = if (color == color1) {
                    color2
                } else {
                    color1
                }
            }
            html += "<font color=$color><b>$character</b></font>"
        }

        return html
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val greedyGooseText = findViewById<TextView>(R.id.textView)
        val greedy = getColoredSpanned("Greedy Goose", "#e83372", "#E191CA")
        greedyGooseText.setText(Html.fromHtml(greedy));
        greedyGooseText.setTextSize(80F)


//        binding.foreGroundButton.setOnClickListener {
//            checkOverlayPermission()
//            startService()
//        }
    }

    // method for starting the service
    private fun startService() {
        // check if the user has already granted
        // the Draw over other apps permission
        if (Settings.canDrawOverlays(this)) {
            // start the service based on the android version
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(Intent(this, ForegroundService::class.java))
            } else {
                startService(Intent(this, ForegroundService::class.java))
            }
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
