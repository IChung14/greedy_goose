package com.example.greedygoose.ui

import android.content.Intent
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.greedygoose.Entertainment
import com.example.greedygoose.R
import com.example.greedygoose.SettingsPage
import com.example.greedygoose.databinding.FragmentHomeBinding
import com.example.greedygoose.mod
import com.example.greedygoose.TimerPage

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater)

        val greedyGooseText = binding.textView
        val greedy = getColoredSpanned("Greedy Goose", "#e83372", "#E191CA")
        greedyGooseText.text = Html.fromHtml(greedy)
        greedyGooseText.textSize = 90F

        val entertainmentButton = binding.entertainmentButton
        entertainmentButton.setOnClickListener {
            startActivity(Intent(context, Entertainment::class.java))
        }

        val productivityButton = binding.productivityButton
        productivityButton.setOnClickListener {
            startActivity(Intent(context, TimerPage::class.java))
        }

        val settingsButton = binding.settingsButton
        settingsButton.setOnClickListener {
            startActivity(Intent(context, SettingsPage::class.java))
        }

        return binding.root
    }


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
}