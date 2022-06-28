package com.example.greedygoose

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.greedygoose.databinding.ActivitySettingsPageBinding


class SettingsPage : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val tv1 = binding.eggCount
        tv1.text = mod.get_egg_count().toString()

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        if (!is_tie_unlocked) {
            binding.customizationOption1.setBackgroundResource(R.drawable.grey_tie)
            binding.customizationOption1.text = "50"
            binding.customizationOption1.textAlignment = View.TEXT_ALIGNMENT_CENTER
            binding.customizationOption1.setTextColor(Color.WHITE)
            binding.customizationOption1.setTextSize(35F)
        }
        if (!is_goggle_unlocked) {
            binding.customizationOption2.setBackgroundResource(R.drawable.grey_goggles)
            binding.customizationOption2.text = "50"
            binding.customizationOption2.textAlignment = View.TEXT_ALIGNMENT_CENTER
            binding.customizationOption2.setTextColor(Color.WHITE)
            binding.customizationOption2.setTextSize(35F)
        }
        if (!is_hard_hat_unlocked) {
            binding.customizationOption3.setBackgroundResource(R.drawable.grey_hardhat)
            binding.customizationOption3.text = "50"
            binding.customizationOption3.textAlignment = View.TEXT_ALIGNMENT_CENTER
            binding.customizationOption3.setTextColor(Color.WHITE)
            binding.customizationOption3.setTextSize(35F)
        }

        binding.customizationOption1.setOnClickListener {
            if (is_tie_unlocked) {
                if (is_tie_selected) {
                    is_tie_selected = false
                    binding.customizationOption1.setBackgroundResource(R.drawable.pink_tie)
                    current_goose = "goose"
                }
                else {
                    is_tie_selected = true
                    is_goggle_selected = false
                    is_hard_hat_selected = false
                    checkGoose()
                }
            }
            else {
                purchase_item("pink", binding.customizationOption1.text.toString().toInt())
            }
        }
        binding.customizationOption2.setOnClickListener {
            if (is_goggle_unlocked) {
                if (is_goggle_selected) {
                    is_goggle_selected = false
                    binding.customizationOption2.setBackgroundResource(R.drawable.science_goggle)
                    current_goose = "goose"
                }
                else {
                    is_tie_selected = false
                    is_goggle_selected = true
                    is_hard_hat_selected = false
                    checkGoose()
                }
            }
            else {
                purchase_item("blue", binding.customizationOption2.text.toString().toInt())
            }
        }
        binding.customizationOption3.setOnClickListener {
            if (is_hard_hat_unlocked) {
                if (is_hard_hat_selected) {
                    is_hard_hat_selected = false
                    binding.customizationOption3.setBackgroundResource(R.drawable.yellow_hard_hat)
                    current_goose = "goose"
                }
                else {
                    is_tie_selected = false
                    is_goggle_selected = false
                    is_hard_hat_selected = true
                    checkGoose()
                }
            }
            else {
                purchase_item("yellow", binding.customizationOption3.text.toString().toInt())
            }
        }

        // We want to listen to any changes in the egg count, and update the number of eggs
        // displayed in our UI in real time
        mod.observe_egg(this, tv1)
        mod.observe_entertainment(this, this)


    }

    private fun updateSelectedItem() {
        if (is_tie_selected) {
            binding.customizationOption1.setBackgroundResource(R.drawable.highlighted_tie)
            current_goose = "pink"
            mod.set_theme("MATH")
        }
        else if (is_tie_unlocked) {
            binding.customizationOption1.setBackgroundResource(R.drawable.pink_tie)
        }
        if (is_goggle_selected) {
            binding.customizationOption2.setBackgroundResource(R.drawable.highlighted_goggles)
            current_goose = "blue"
            mod.set_theme("SCI")
        }
        else if (is_goggle_unlocked) {
            binding.customizationOption2.setBackgroundResource(R.drawable.science_goggle)
        }
        if (is_hard_hat_selected) {
            binding.customizationOption3.setBackgroundResource(R.drawable.highlighted_hardhat)
            current_goose = "yellow"
            mod.set_theme("ENG")
        }
        else if (is_hard_hat_unlocked) {
            binding.customizationOption3.setBackgroundResource(R.drawable.yellow_hard_hat)
        }
    }
    private fun updateItem(item_name: String) {
        if (item_name === "pink") {
            is_tie_unlocked = true
            is_tie_selected = true
            is_goggle_selected = false
            is_hard_hat_selected = false
            binding.customizationOption1.setBackgroundResource(R.drawable.highlighted_tie)
            binding.customizationOption1.text = ""
            updateSelectedItem()
        }
        else if (item_name === "blue") {
            is_goggle_unlocked = true
            is_tie_selected = false
            is_goggle_selected = true
            is_hard_hat_selected = false
            binding.customizationOption2.setBackgroundResource(R.drawable.highlighted_goggles)
            binding.customizationOption2.text = ""
            updateSelectedItem()
        }
        else if (item_name === "yellow") {
            is_hard_hat_unlocked = true
            is_tie_selected = false
            is_goggle_selected = false
            is_hard_hat_selected = true
            binding.customizationOption3.setBackgroundResource(R.drawable.highlighted_hardhat)
            binding.customizationOption3.text = ""
            updateSelectedItem()
        }
        binding.eggCount.text = mod.get_egg_count().toString()
    }

    private fun purchase_item(item_name: String, item_price: Int) {
        if(mod.get_egg_count()!! >= item_price){
            mod.decrease_egg_count(item_price)
            updateItem(item_name)
        }
    }

    private fun checkGoose() {
        updateSelectedItem()
    }

    companion object {
        var is_tie_unlocked = false
        var is_goggle_unlocked = false
        var is_hard_hat_unlocked = true
        var is_tie_selected = false
        var is_goggle_selected = false
        var is_hard_hat_selected = false
        var current_goose = "goose"
    }
}
