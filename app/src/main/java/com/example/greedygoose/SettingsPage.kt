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
        val tv1 = binding.eggCount
        tv1.text = mod.get_egg_count().toString()

        val backButton = binding.backImageButton
        backButton.setOnClickListener {
            startActivity(Intent(this@SettingsPage, MainActivity::class.java))
        }

        // We want to listen to any changes in the egg count, and update the number of eggs
        // displayed in our UI in real time
        mod.observe_egg(this, tv1)

    }
}


