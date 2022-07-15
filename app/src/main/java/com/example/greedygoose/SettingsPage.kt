package com.example.greedygoose

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.greedygoose.data.Theme
import com.example.greedygoose.databinding.ActivitySettingsPageBinding


class SettingsPage : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsPageBinding
    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsPageBinding.inflate(layoutInflater)
        viewModel = SettingsViewModel(context = this)

        setContentView(binding.root)

        viewModel.eggCount.observe(this){
            binding.eggCount.text = it.toString()
        }
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        if (!mod.is_tie_unlocked) {
            binding.customizationOption1.setBackgroundResource(R.drawable.grey_tie)
            binding.customizationOption1.text = "50"
            binding.customizationOption1.textAlignment = View.TEXT_ALIGNMENT_CENTER
            binding.customizationOption1.setTextColor(Color.WHITE)
            binding.customizationOption1.setTextSize(35F)
        }
        if (!mod.is_goggle_unlocked) {
            binding.customizationOption2.setBackgroundResource(R.drawable.grey_goggles)
            binding.customizationOption2.text = "25"
            binding.customizationOption2.textAlignment = View.TEXT_ALIGNMENT_CENTER
            binding.customizationOption2.setTextColor(Color.WHITE)
            binding.customizationOption2.setTextSize(35F)
        }
        if (!mod.is_hard_hat_unlocked) {
            binding.customizationOption3.setBackgroundResource(R.drawable.grey_hardhat)
            binding.customizationOption3.text = "50"
            binding.customizationOption3.textAlignment = View.TEXT_ALIGNMENT_CENTER
            binding.customizationOption3.setTextColor(Color.WHITE)
            binding.customizationOption3.setTextSize(35F)
        }

        when(viewModel.theme.value){
            Theme.SCI -> binding.customizationOption2.setBackgroundResource(R.drawable.highlighted_goggles)
            Theme.ENG -> binding.customizationOption3.setBackgroundResource(R.drawable.highlighted_hardhat)
            Theme.MATH -> binding.customizationOption1.setBackgroundResource(R.drawable.highlighted_tie)
            Theme.NONE -> {}
        }

        binding.customizationOption1.setOnClickListener {
            if (mod.is_tie_unlocked) {
                if (viewModel.theme.value == Theme.MATH) {
                    binding.customizationOption1.setBackgroundResource(R.drawable.pink_tie)
                    viewModel.setTheme(Theme.NONE)
                } else checkGoose()
            } else purchaseItem("pink", binding.customizationOption1.text.toString().toInt())
        }
        binding.customizationOption2.setOnClickListener {
            if (mod.is_goggle_unlocked) {
                if (viewModel.theme.value == Theme.SCI) {
                    binding.customizationOption2.setBackgroundResource(R.drawable.science_goggle)
                    viewModel.setTheme(Theme.NONE)
                } else checkGoose()
            } else purchaseItem("blue", binding.customizationOption2.text.toString().toInt())
        }
        binding.customizationOption3.setOnClickListener {
            if (mod.is_hard_hat_unlocked) {
                if (viewModel.theme.value == Theme.ENG) {
                    binding.customizationOption3.setBackgroundResource(R.drawable.yellow_hard_hat)
                    viewModel.setTheme(Theme.NONE)
                } else checkGoose()
            } else purchaseItem("yellow", binding.customizationOption3.text.toString().toInt())
        }

        // We want to listen to any changes in the egg count, and update the number of eggs
        // displayed in our UI in real time
//        mod.observeEntertainment(this, this)
    }

    private fun updateSelectedItem() {
        if (mod.is_tie_selected) {
            binding.customizationOption1.setBackgroundResource(R.drawable.highlighted_tie)
            viewModel.setTheme(Theme.MATH)
        }else if (mod.is_tie_unlocked) {
            binding.customizationOption1.setBackgroundResource(R.drawable.pink_tie)
        }

        if (mod.is_goggle_selected) {
            binding.customizationOption2.setBackgroundResource(R.drawable.highlighted_goggles)
            viewModel.setTheme(Theme.SCI)
        }else if (mod.is_goggle_unlocked) {
            binding.customizationOption2.setBackgroundResource(R.drawable.science_goggle)
        }

        if (mod.is_hard_hat_selected) {
            binding.customizationOption3.setBackgroundResource(R.drawable.highlighted_hardhat)
            viewModel.setTheme(Theme.ENG)
        }else if (mod.is_hard_hat_unlocked) {
            binding.customizationOption3.setBackgroundResource(R.drawable.yellow_hard_hat)
        }
    }
    private fun updateItem(item_name: String) {
        if (item_name === "pink") {
            mod.is_tie_unlocked = true
            mod.is_tie_selected = true
            mod.is_goggle_selected = false
            mod.is_hard_hat_selected = false
            binding.customizationOption1.setBackgroundResource(R.drawable.highlighted_tie)
            binding.customizationOption1.text = ""
            updateSelectedItem()
        }
        else if (item_name === "blue") {
            mod.is_goggle_unlocked = true
            mod.is_tie_selected = false
            mod.is_goggle_selected = true
            mod.is_hard_hat_selected = false
            binding.customizationOption2.setBackgroundResource(R.drawable.highlighted_goggles)
            binding.customizationOption2.text = ""
            updateSelectedItem()
        }
        else if (item_name === "yellow") {
            mod.is_hard_hat_unlocked = true
            mod.is_tie_selected = false
            mod.is_goggle_selected = false
            mod.is_hard_hat_selected = true
            binding.customizationOption3.setBackgroundResource(R.drawable.highlighted_hardhat)
            binding.customizationOption3.text = ""
            updateSelectedItem()
        }
    }

    private fun purchaseItem(item_name: String, item_price: Int) {
        if(viewModel.eggCount.value >= item_price){
            viewModel.decrementEggCount(item_price)
            updateItem(item_name)
        }
    }

    private fun checkGoose() {
        updateSelectedItem()
    }
}
