package com.example.greedygoose

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.greedygoose.databinding.ActivityMainBinding
import com.example.greedygoose.ui.HomeFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction().apply {
            replace(binding.fragmentContainerView.id, HomeFragment())
            commit()
        }
    }
}