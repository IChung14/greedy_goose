package com.example.greedygoose

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Html
import com.example.greedygoose.databinding.ActivityMainBinding

var mod: model = model()

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private fun getColoredSpanned(text: String, color1: String, color2: String): String? {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mod.observe_entertainment(this, this)

        val greedyGooseText = binding.textView
        val greedy = getColoredSpanned("Greedy Goose", "#e83372", "#E191CA")
        greedyGooseText.setText(Html.fromHtml(greedy));
        greedyGooseText.setTextSize(90F)

        val entertainmentButton = binding.entertainmentButton
        entertainmentButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, Entertainment::class.java))
            Handler().postDelayed({finish()}, 5000)
        }

        val settingsButton = binding.settingsButton
        settingsButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, SettingsPage::class.java))
        }
    }
}
