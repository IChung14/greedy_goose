package com.example.greedygoose

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
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

        setAccessoryWidget(binding.customizationOption1, Theme.MATH, 50)
        setAccessoryWidget(binding.customizationOption2, Theme.SCI, 25)
        setAccessoryWidget(binding.customizationOption3, Theme.ENG, 50)
    }

    // initialize accessory widget
    private fun setAccessoryWidget(view: CheckBox, theme: Theme, price: Int){
        // make each view observe the theme and purchased changes
        viewModel.theme.observe(this){
            updateWidgetSrc(view, theme, price)
        }
        viewModel.purchased[theme]?.observe(this){
            updateWidgetSrc(view, theme, price)
        }

        view.setOnClickListener {
            if (viewModel.purchased[theme]?.value == true || viewModel.setPurchased(theme, price)) {
                // if purchased, toggle selected accessory usage
                toggleAccessoryUsage(view, theme)
            }
        }
    }

    private fun updateWidgetSrc(view: CheckBox, theme: Theme, price: Int){
        view.setBackgroundResource(viewModel.getThemeSrc(theme))

        if(viewModel.purchased[theme]?.value != true){
            // display gray icon if not purchased
            view.text = price.toString()
            view.textAlignment = View.TEXT_ALIGNMENT_CENTER
            view.setTextColor(Color.WHITE)
            view.textSize = 35F
        }else{
            view.text = ""
        }
    }

    // toggle between active and inactive accessory status
    private fun toggleAccessoryUsage(view: CheckBox, theme: Theme) {
        // checks if this performance is select or unselect
        val isSelected = viewModel.theme.value != theme
//        val src = viewModel.getThemeSrc(theme, isSelected)
//
//        view.setBackgroundResource(src)

        viewModel.setTheme(if(isSelected) theme else Theme.NONE)
    }

    // set all accessory widgets to default image
    private fun initAllAccessories(){
        binding.customizationOption1.setBackgroundResource(R.drawable.pink_tie)
        binding.customizationOption2.setBackgroundResource(R.drawable.science_goggle)
        binding.customizationOption3.setBackgroundResource(R.drawable.yellow_hard_hat)
    }
}
