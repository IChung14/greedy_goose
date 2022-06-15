package com.example.greedygoose

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.greedygoose.databinding.ActivitySettingsPageBinding
import android.R
import android.view.View

import android.widget.TextView




class SettingsPage : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsPageBinding
    public var egg_count = "100"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val tv1 = binding.eggCount
        tv1.text = egg_count

        val backButton = binding.backImageButton
        backButton.setOnClickListener {
            startActivity(Intent(this@SettingsPage, MainActivity::class.java))
        }
    }
}