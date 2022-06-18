package com.example.greedygoose

import android.R
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.greedygoose.databinding.ActivitySettingsPageBinding


class SettingsPage : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsPageBinding
    public var egg_count = "100"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val tv1 = binding.eggCount
        tv1.text = egg_count

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }
}