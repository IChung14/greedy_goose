package com.example.greedygoose

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.greedygoose.databinding.ActivitySettingsPageBinding

class SettingsPage : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val backButton = binding.backImageButton
        backButton.setOnClickListener {
            startActivity(Intent(this@SettingsPage, MainActivity::class.java))
        }
    }
}