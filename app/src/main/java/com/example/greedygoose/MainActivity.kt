package com.example.greedygoose

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.Html
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.greedygoose.databinding.ActivityMainBinding



class model {
    private var egg_count = MutableLiveData<Int>(100)
    private var theme = "MATH"

    fun increase_egg_count(amt: Int) {
        this.egg_count.value = this.egg_count.value?.plus(amt)
    }

    fun decrease_egg_count(amt: Int) {
        this.egg_count.value = this.egg_count.value?.minus(amt)

    }

    fun get_egg_count(): Int? {
        return this.egg_count.value
    }

    // Pass in a UI element that you want to be updated based on the egg_count
    // For now, only taking in a textview
    fun observe_egg(context:LifecycleOwner, ob: TextView) {
        this.egg_count.observe(context, Observer {
            ob.text = this.egg_count.value.toString()
        })
    }
}

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
