package com.example.greedygoose

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
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
            startActivity(Intent(this, Entertainment::class.java))
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
}